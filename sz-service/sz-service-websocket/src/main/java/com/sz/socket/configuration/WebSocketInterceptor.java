package com.sz.socket.configuration;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.jwt.JWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

import static com.sz.socket.cache.SocketManagerCache.LOGIN_ID;
import static com.sz.socket.cache.SocketManagerCache.SEC_WEBSOCKET_PROTOCOL_HEADER;
import static com.sz.socket.cache.SocketManagerCache.TOKEN;

/**
 * WebSocket 握手鉴权拦截器
 * <p>
 * 鉴权流程（针对 sa-token JWT Simple 模式）：
 * <ol>
 * <li>从 {@code Sec-WebSocket-Protocol} 请求头取 token（兼容大小写、空格、逗号分隔多值）</li>
 * <li>{@link SaJwtUtil#parseToken} 验证 JWT 签名及 loginType</li>
 * <li>从 JWT payload 直接取 loginId（Simple 模式不写 token→loginId Redis 映射，不能用
 * getLoginIdByToken）</li>
 * <li>验证 token-session 在 Redis 存在（session 存在 = 登录态有效，未被注销/踢出）</li>
 * <li>账号封禁判定（{@link StpUtil#isDisable}）</li>
 * <li>刷新
 * active-timeout（{@link cn.dev33.satoken.stp.StpLogic#updateLastActiveToNow}）</li>
 * </ol>
 *
 * @author sz
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * 握手之前
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param wsHandler
     *            handler
     * @param attributes
     *            属性
     * @return 是否握手成功：true-成功，false-失败
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
        ServletServerHttpResponse serverHttpResponse = (ServletServerHttpResponse) response;

        String token = resolveToken(serverHttpRequest.getHeaders());
        if (token == null || token.isEmpty()) {
            log.warn("【websocket】握手鉴权失败：未携带 token");
            return false;
        }

        try {
            // 1. 验证 JWT 签名及 loginType
            String secretKey = StpUtil.getStpLogic().getConfigOrGlobal().getJwtSecretKey();
            JWT jwt = SaJwtUtil.parseToken(token, StpUtil.getStpLogic().getLoginType(), secretKey, false);
            if (!jwt.verify()) {
                log.warn("【websocket】握手鉴权失败：JWT 签名验证不通过, token=[{}]", desensitize(token));
                return false;
            }

            // 2. 从 JWT payload 取 loginId（Simple 模式 loginId 在 payload 中）
            Object loginIdObj = jwt.getPayload().getClaim(SaJwtUtil.LOGIN_ID);
            if (loginIdObj == null) {
                log.warn("【websocket】握手鉴权失败：JWT payload 中无 loginId, token=[{}]", desensitize(token));
                return false;
            }
            String loginId = loginIdObj.toString();

            // 3. 验证 token-session 存在于 Redis（session 不存在说明已注销/被踢出）
            String sessionKey = StpUtil.getStpLogic().splicingKeyTokenSession(token);
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey))) {
                log.warn("【websocket】握手鉴权失败：token-session 不存在（已注销或被踢出）, loginId=[{}]", loginId);
                return false;
            }

            // 4. 账号封禁判定
            if (StpUtil.isDisable(loginId)) {
                log.warn("【websocket】握手鉴权失败：账号已被封禁, loginId=[{}]", loginId);
                return false;
            }

            // 5. 刷新 active-timeout
            StpUtil.getStpLogic().updateLastActiveToNow(token);

            attributes.put(LOGIN_ID, loginId);
            attributes.put(TOKEN, token);
            log.info("【websocket】握手成功，loginId=[{}]", loginId);

            // 回写子协议头（浏览器 WebSocket API 要求服务端确认所选子协议）
            serverHttpResponse.getServletResponse().setHeader(SEC_WEBSOCKET_PROTOCOL_HEADER, token);
            return true;

        } catch (SaTokenException e) {
            log.warn("【websocket】握手鉴权失败：{}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("【websocket】握手鉴权异常", e);
            return false;
        }
    }

    /**
     * 握手后
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 握手后 ...
    }

    /**
     * 解析 Sec-WebSocket-Protocol 头中的 token。
     * <p>
     * 容错处理：
     * <ul>
     * <li>HTTP 头名称大小写不敏感（{@link HttpHeaders} 已实现）</li>
     * <li>支持单值 / 多值（逗号或多 header 行）</li>
     * <li>逐项 trim，过滤空串</li>
     * <li>取第一个非空值</li>
     * </ul>
     */
    private String resolveToken(HttpHeaders headers) {
        List<String> values = headers.get(SEC_WEBSOCKET_PROTOCOL_HEADER);
        if (values == null || values.isEmpty()) {
            return null;
        }
        for (String raw : values) {
            if (raw == null)
                continue;
            for (String part : raw.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    return trimmed;
                }
            }
        }
        return null;
    }

    /**
     * token 日志脱敏：仅保留前后各 4 位
     */
    private String desensitize(String token) {
        if (token.length() <= 8)
            return "***";
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }
}
