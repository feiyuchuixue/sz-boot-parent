package com.sz.socket.cache;

import com.sz.core.common.entity.WsSession;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author sz
 * @since 2023/9/6 17:01
 */
@Slf4j
public class SocketManagerCache {

    public static final String SEC_WEBSOCKET_PROTOCOL_HEADER = "sec-websocket-protocol";

    public static final String LOGIN_ID = "loginId";

    /**
     * WebSocket 会话上下文中保存的 token key
     */
    public static final String TOKEN = "token";

    /**
     * 心跳请求频道（客户端 → 服务端）
     */
    public static final String CHANNEL_PING = "PING";

    /**
     * 心跳响应频道（服务端 → 客户端）
     */
    public static final String CHANNEL_PONG = "PONG";

    /**
     * session attribute key：上次刷新 active-timeout 的时间戳（毫秒）。 用于节流
     * updateLastActiveToNow，避免每条消息都触发 Redis 写操作。
     */
    public static final String LAST_REFRESH_AT = "lastRefreshAt";

    /**
     * 鉴权失效时使用的自定义 WebSocket 关闭码（应用区段 4000-4999）。 前端识别此码后应停止重连并跳转登录页。
     */
    public static final int CLOSE_CODE_AUTH_EXPIRED = 4401;

    /**
     * sid <==> session 关系
     */
    public static ConcurrentHashMap<String, WsSession> onlineSessionMap = new ConcurrentHashMap<>();

    /**
     * loginId <==> sid 关系; 1对多,一个用户有可能在多个浏览器上登录
     */
    public static ConcurrentHashMap<String, List<String>> onlineUserSessionIdMap = new ConcurrentHashMap<>();

    public static void addOnlineSid(String loginId, String sid) {
        // 使用 compute 保证 check-and-act 原子；List 用 CopyOnWriteArrayList 应对并发遍历
        onlineUserSessionIdMap.compute(loginId, (k, existing) -> {
            List<String> sids = (existing == null) ? new CopyOnWriteArrayList<>() : existing;
            if (!sids.contains(sid)) {
                sids.add(sid);
            }
            return sids;
        });
    }

    /**
     * 清除断线的sid
     *
     * @param sid
     *            sid
     */
    public static void removeUserSession(String sid) {
        WsSession wsSession = onlineSessionMap.get(sid);
        if (wsSession == null) {
            // 已被清理或从未注册，幂等返回
            return;
        }
        String loginId = wsSession.getLoginId();
        if (loginId == null) {
            return;
        }
        onlineUserSessionIdMap.computeIfPresent(loginId, (k, sids) -> {
            sids.remove(sid);
            return sids.isEmpty() ? null : sids;
        });
    }

}
