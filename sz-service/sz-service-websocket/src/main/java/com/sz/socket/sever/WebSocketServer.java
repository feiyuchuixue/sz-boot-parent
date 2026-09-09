package com.sz.socket.sever;

import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.sz.core.common.entity.ClientMessage;
import com.sz.core.common.entity.SocketPushMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.entity.WsSession;
import com.sz.core.util.JsonUtils;
import com.sz.core.util.SocketUtil;
import com.sz.redis.WebsocketRedisService;
import com.sz.socket.cache.SocketManagerCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.sz.socket.cache.SocketManagerCache.CHANNEL_PING;
import static com.sz.socket.cache.SocketManagerCache.CHANNEL_PONG;
import static com.sz.socket.cache.SocketManagerCache.CLOSE_CODE_AUTH_EXPIRED;
import static com.sz.socket.cache.SocketManagerCache.LAST_REFRESH_AT;
import static com.sz.socket.cache.SocketManagerCache.LOGIN_ID;
import static com.sz.socket.cache.SocketManagerCache.TOKEN;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketServer extends TextWebSocketHandler {

    /**
     * updateLastActiveToNow 节流阈值：5 分钟内同一 session 只写一次 Redis。 鉴权读操作（签名验证 / hasKey /
     * isDisable）始终执行，不受节流影响。
     */
    private static final long REFRESH_THROTTLE_MS = 5 * 60 * 1000L;

    private final WebsocketRedisService websocketRedisService;

    private final ServerState serverState;

    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * 接受客户端消息
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        if (serverState.isShuttingDown()) {
            log.warn("Service is shutting down. Ignoring new messages.");
            return;
        }

        String sid = session.getId();
        String token = (String) session.getAttributes().get(TOKEN);

        // ── 在 SocketMessage 反序列化前先提取原始 channel 字符串 ──
        // PING/PONG 不在 SocketChannelEnum 中，直接反序列化会导致 channel 为 null；
        // 先从原始 JSON 读字符串，可靠判断心跳报文。
        String rawChannel = null;
        try {
            rawChannel = JSONUtil.parseObj(message.getPayload()).getStr("channel");
        } catch (Exception ignore) {
            // 非 JSON 格式，保持 null，后续走正常错误路径
        }

        // ── PING 快速路径：跳过完整鉴权，仅刷新 active-timeout，回复 PONG ──
        if (CHANNEL_PING.equals(rawChannel)) {
            handlePing(session, token);
            return;
        }

        // ── 非心跳消息：先完整鉴权 ──
        if (!checkAuthAndRefresh(session, token)) {
            return;
        }

        // 解析消息体
        ClientMessage msg;
        try {
            msg = JsonUtils.parseObject(message.getPayload(), ClientMessage.class);
        } catch (Exception e) {
            log.error("【websocket】消息解析失败，payload: {}, error: {}", message.getPayload(), e.getMessage());
            session.sendMessage(new TextMessage("{\"error\":\"消息格式错误\"}"));
            return;
        }
        if (msg == null) {
            log.warn("【websocket】消息解析结果为null，payload: {}", message.getPayload());
            session.sendMessage(new TextMessage("{\"error\":\"消息格式错误\"}"));
            return;
        }

        // channel null 保护（前端未传 channel 时为 null，防 NPE）
        if (msg.getChannel() == null) {
            log.warn("【websocket】消息 channel 为 null，已忽略，sid=[{}], payload: {}", sid, message.getPayload());
            return;
        }

        switch (msg.getChannel()) {
            default :
                log.debug("【websocket】未识别 channel，透传至业务服务，sid=[{}], message: {}", sid, message.getPayload());
                TransferMessage tm = new TransferMessage();
                SocketPushMessage echo = new SocketPushMessage(msg.getChannel(), msg.getData());
                tm.setMessage(echo);
                String channelUsername = websocketRedisService.getUserBySessionId(sid);
                if (channelUsername != null) {
                    tm.setFromUser(channelUsername);
                }
                websocketRedisService.sendWsToService(tm);
                break;
        }
    }

    /**
     * 处理心跳 PING：刷新 active-timeout，立即回复 PONG。
     * <p>
     * 不做完整鉴权：token 在握手时已验证并存入 session attributes（客户端不可篡改），
     * {@link WebSocketSessionGuard} 每 60s 做全量兜底扫描。
     */
    private void handlePing(WebSocketSession session, String token) {
        try {
            if (token != null && !token.isEmpty()) {
                StpUtil.getStpLogic().updateLastActiveToNow(token);
            }
            session.sendMessage(new TextMessage("{\"channel\":\"" + CHANNEL_PONG + "\"}"));
        } catch (Exception e) {
            log.warn("【websocket】处理 PING 异常，sid=[{}], err={}", session.getId(), e.getMessage());
        }
    }

    /**
     * 连接建立后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (serverState.isShuttingDown()) {
            log.warn("Service is shutting down. Rejecting new connection.");
            session.close(CloseStatus.SERVICE_RESTARTED);
            return;
        }
        String sid = session.getId();
        String loginId = (String) session.getAttributes().get(LOGIN_ID);
        WsSession wsSession = new WsSession(sid, loginId, session);

        SocketManagerCache.onlineSessionMap.put(sid, wsSession);
        SocketManagerCache.addOnlineSid(loginId, sid);
        websocketRedisService.addUserToOnlineChat(sid, loginId);
        if (log.isDebugEnabled()) {
            log.debug("【websocket】连接建立，sid=[{}], loginId=[{}], 当前连接数:{}, 在线人数:{}", sid, loginId, websocketRedisService.getConnectionCount(),
                    websocketRedisService.getOnlineUserCount());
        }
    }

    /**
     * 连接关闭后
     * <p>
     * 注意：进入此方法时连接已经关闭，{@code session.isOpen()} 通常返回 false， 因此清理逻辑必须无条件执行，不能用
     * isOpen() 包裹（旧实现导致清理常态化跳过， 引发 Redis / 内存 Map 残留累积）。
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sid = session.getId();
        String loginId = (String) session.getAttributes().get(LOGIN_ID);
        log.info("【websocket】连接断开，sid=[{}], loginId=[{}], status=[{}]", sid, loginId, status);

        if (serverState.isShuttingDown()) {
            // 停机阶段由 disconnectAll() 统一清理 Redis + 内存，此处不重复
            return;
        }

        try {
            websocketRedisService.removeUserBySessionId(sid);
        } catch (Exception e) {
            log.error("【websocket】清理 Redis 在线状态异常，sid=[{}]", sid, e);
        }
        try {
            // 注意删除顺序：先清 loginId→sid 反向索引，再清 sid→session 主索引
            SocketManagerCache.removeUserSession(sid);
            SocketManagerCache.onlineSessionMap.remove(sid);
        } catch (Exception e) {
            log.error("【websocket】清理内存会话缓存异常，sid=[{}]", sid, e);
        }
    }

    /**
     * 校验 token 是否仍然有效，并节流刷新活跃时间。
     * <p>
     * 节流规则：距上次 {@code updateLastActiveToNow} 调用 <
     * {@code REFRESH_THROTTLE_MS}（5min）时跳过写操作。 签名验证 / Redis hasKey / 封禁判定等读操作始终执行。
     *
     * @return true=有效；false=失效（已主动关闭连接）
     */
    private boolean checkAuthAndRefresh(WebSocketSession session, String token) {
        if (token == null || token.isEmpty()) {
            closeQuietly(session, new CloseStatus(CLOSE_CODE_AUTH_EXPIRED, "auth missing"));
            return false;
        }
        try {
            // 1. JWT 签名验证（本地运算，无 IO）
            String secretKey = StpUtil.getStpLogic().getConfigOrGlobal().getJwtSecretKey();
            JWT jwt = SaJwtUtil.parseToken(token, StpUtil.getStpLogic().getLoginType(), secretKey, false);
            // 2. token-session 存在性（Redis 读）
            String sessionKey = StpUtil.getStpLogic().splicingKeyTokenSession(token);
            boolean sessionExists = Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
            Object loginIdObj = sessionExists ? jwt.getPayload().getClaim(SaJwtUtil.LOGIN_ID) : null;
            // 3. 封禁判定（Redis 读）
            if (!jwt.verify() || !sessionExists || loginIdObj == null || StpUtil.isDisable(loginIdObj)) {
                log.warn("【websocket】token 已失效，主动关闭连接，sid=[{}]", session.getId());
                closeQuietly(session, new CloseStatus(CLOSE_CODE_AUTH_EXPIRED, "auth expired"));
                return false;
            }
            // 4. 节流：5 分钟内只做一次 active-timeout 刷新（Redis 写）
            long now = System.currentTimeMillis();
            Object lastRefreshObj = session.getAttributes().get(LAST_REFRESH_AT);
            long lastRefresh = lastRefreshObj instanceof Long ? (Long) lastRefreshObj : 0L;
            if (now - lastRefresh >= REFRESH_THROTTLE_MS) {
                StpUtil.getStpLogic().updateLastActiveToNow(token);
                session.getAttributes().put(LAST_REFRESH_AT, now);
            }
            return true;
        } catch (Exception e) {
            log.warn("【websocket】鉴权校验异常，sid=[{}], err={}", session.getId(), e.getMessage());
            closeQuietly(session, new CloseStatus(CLOSE_CODE_AUTH_EXPIRED, "auth error"));
            return false;
        }
    }

    private void closeQuietly(WebSocketSession session, CloseStatus status) {
        try {
            if (session.isOpen()) {
                session.close(status);
            }
        } catch (IOException ignore) {
            // 关闭过程中的 IO 异常忽略
        }
    }

    /**
     * 根据 loginId 定向推送消息。 单个连接发送失败不影响其余连接（每次迭代独立 try-catch）。
     */
    public void sendMessage(List<String> loginIds, SocketPushMessage socketMessage) {
        log.info("定向推送。推送用户范围:{}, message: {}", loginIds, JsonUtils.toJsonString(socketMessage));
        if (loginIds == null || loginIds.isEmpty()) {
            return;
        }
        for (String loginId : loginIds) {
            List<String> notifyUserSids = SocketManagerCache.onlineUserSessionIdMap.get(loginId);
            if (notifyUserSids == null)
                continue;
            for (String notifyUserSid : notifyUserSids) {
                WsSession wsSession = SocketManagerCache.onlineSessionMap.get(notifyUserSid);
                if (wsSession == null) {
                    log.info("websocket 定向推送失败，session 不存在，loginId=[{}], sid=[{}]", loginId, notifyUserSid);
                    continue;
                }
                try {
                    wsSession.getSession().sendMessage(new TextMessage(SocketUtil.serialize(socketMessage)));
                } catch (IOException e) {
                    log.warn("websocket 定向推送失败，loginId=[{}], sid=[{}], err={}", loginId, notifyUserSid, e.getMessage());
                }
            }
        }
    }

    /**
     * 全员推送。 单个连接发送失败不影响其余连接（每次迭代独立 try-catch）。
     */
    public void sendMessageToAllUser(SocketPushMessage socketMessage) {
        log.info("全员推送。message: {}", JsonUtils.toJsonString(socketMessage));
        List<String> allOnlineUsernames = new ArrayList<>(SocketManagerCache.onlineUserSessionIdMap.keySet());
        for (String loginId : allOnlineUsernames) {
            List<String> notifyUserSids = SocketManagerCache.onlineUserSessionIdMap.get(loginId);
            if (notifyUserSids == null)
                continue;
            for (String notifyUserSid : notifyUserSids) {
                WsSession wsSession = SocketManagerCache.onlineSessionMap.get(notifyUserSid);
                if (wsSession == null)
                    continue;
                try {
                    wsSession.getSession().sendMessage(new TextMessage(SocketUtil.serialize(socketMessage)));
                } catch (IOException e) {
                    log.warn("websocket 全员推送失败，loginId=[{}], sid=[{}], err={}", loginId, notifyUserSid, e.getMessage());
                }
            }
        }
    }

    public void disconnectAll() {
        ConcurrentHashMap<String, WsSession> onlineSessionMap = SocketManagerCache.onlineSessionMap;
        for (Map.Entry<String, WsSession> sessionEntry : onlineSessionMap.entrySet()) {
            String sid = sessionEntry.getKey();
            WsSession wsSession = sessionEntry.getValue();
            try {
                websocketRedisService.removeUserBySessionId(sid);
            } catch (Exception e) {
                log.error("【websocket】停机清理 Redis 异常，sid=[{}]", sid, e);
            }
            WebSocketSession session = wsSession.getSession();
            if (session != null) {
                try {
                    session.close(CloseStatus.SERVICE_RESTARTED);
                } catch (IOException e) {
                    log.error("【websocket】停机关闭连接异常，sid=[{}]", sid, e);
                }
            }
        }
        log.info("【websocket】优雅退出，已断开全部连接，count={}", onlineSessionMap.size());
        SocketManagerCache.onlineSessionMap.clear();
        SocketManagerCache.onlineUserSessionIdMap.clear();
    }

}
