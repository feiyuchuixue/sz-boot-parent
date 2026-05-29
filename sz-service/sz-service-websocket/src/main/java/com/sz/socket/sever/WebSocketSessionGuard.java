package com.sz.socket.sever;

import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.jwt.JWT;
import com.sz.core.common.entity.WsSession;
import com.sz.socket.cache.SocketManagerCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

import static com.sz.socket.cache.SocketManagerCache.CLOSE_CODE_AUTH_EXPIRED;
import static com.sz.socket.cache.SocketManagerCache.TOKEN;

/**
 * WebSocket 会话巡检：定时扫描本节点所有在线 session， 对 token 已失效（被踢/被替换/active-timeout
 * 等）的连接主动断开。
 * <p>
 * 这是对"长连接期间 sa-token 状态变化但 WebSocket 协议不感知"问题的补偿机制。 与握手期、消息入口处的 token
 * 校验共同构成完整的鉴权链路。
 *
 * @author sz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSessionGuard {

    private final ServerState serverState;

    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * 每 60 秒巡检一次
     */
    @Scheduled(fixedDelay = 60_000L, initialDelay = 60_000L)
    public void scan() {
        if (serverState.isShuttingDown()) {
            return;
        }
        int total = 0;
        int closed = 0;
        for (Map.Entry<String, WsSession> entry : SocketManagerCache.onlineSessionMap.entrySet()) {
            total++;
            String sid = entry.getKey();
            WsSession wsSession = entry.getValue();
            WebSocketSession session = wsSession.getSession();
            if (session == null || !session.isOpen()) {
                continue;
            }
            String token = (String) session.getAttributes().get(TOKEN);
            if (token == null || token.isEmpty()) {
                closeQuietly(session, "no-token");
                closed++;
                continue;
            }
            try {
                String secretKey = StpUtil.getStpLogic().getConfigOrGlobal().getJwtSecretKey();
                JWT jwt = SaJwtUtil.parseToken(token, StpUtil.getStpLogic().getLoginType(), secretKey, false);
                String sessionKey = StpUtil.getStpLogic().splicingKeyTokenSession(token);
                boolean sessionExists = Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
                Object loginIdObj = sessionExists ? jwt.getPayload().getClaim(SaJwtUtil.LOGIN_ID) : null;
                if (!jwt.verify() || !sessionExists || loginIdObj == null || StpUtil.isDisable(loginIdObj)) {
                    log.info("【websocket】巡检发现 token 失效，断开连接，sid=[{}]", sid);
                    closeQuietly(session, "auth-invalid");
                    closed++;
                }
            } catch (Exception e) {
                log.warn("【websocket】巡检鉴权异常，sid=[{}], err={}", sid, e.getMessage());
                closeQuietly(session, "auth-error");
                closed++;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("【websocket】会话巡检完成，扫描={}, 主动断开={}", total, closed);
        }
    }

    private void closeQuietly(WebSocketSession session, String reason) {
        try {
            session.close(new CloseStatus(CLOSE_CODE_AUTH_EXPIRED, reason));
        } catch (IOException ignore) {
        }
    }
}
