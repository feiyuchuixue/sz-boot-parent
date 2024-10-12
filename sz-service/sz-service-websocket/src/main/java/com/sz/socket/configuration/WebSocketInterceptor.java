package com.sz.socket.configuration;

import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.jwt.JWT;
import com.sz.core.util.StringUtils;
import com.sz.core.util.Utils;
import com.sz.redis.CommonKeyConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.sz.socket.cache.SocketManagerCache.LOGIN_ID;
import static com.sz.socket.cache.SocketManagerCache.SEC_WEBSOCKET_PROTOCOL_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    private final RedisTemplate redisTemplate;

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
        String authorization = serverHttpRequest.getServletRequest().getHeader(SEC_WEBSOCKET_PROTOCOL_HEADER);
        String keyt = StpUtil.getStpLogic().getConfigOrGlobal().getJwtSecretKey();
        if (Utils.isNotNull(authorization)) {
            JWT jwt = SaJwtUtil.parseToken(authorization, "login", keyt, false);
            if (!jwt.verify()) {
                return false;
            }
            String loginId = jwt.getPayload().getClaimsJson().get(LOGIN_ID).toString();
            String key = StringUtils.getRealKey(CommonKeyConstants.TOKEN_SESSION, authorization);
            // 从redis 里获取用户，验证是否是有效用户,如果失败则中断连接
            if (redisTemplate.hasKey(key)) {
                // 自定义attribute
                attributes.put(LOGIN_ID, loginId);
                log.info("用户: [{}]建立连接, token:[{}]", loginId, authorization);
                serverHttpResponse.getServletResponse().setHeader(SEC_WEBSOCKET_PROTOCOL_HEADER, authorization);
                return true;
            }
        }
        return false;
    }

    /**
     * 握手后
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param wsHandler
     *            wsHandler
     * @param exception
     *            exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
