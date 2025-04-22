package com.sz.socket.sever;

import com.sz.core.common.entity.SocketMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.entity.WsSession;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.JsonUtils;
import com.sz.core.util.SocketUtil;
import com.sz.core.util.Utils;
import com.sz.redis.WebsocketRedisService;
import com.sz.socket.cache.SocketManagerCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

import static com.sz.socket.cache.SocketManagerCache.LOGIN_ID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketServer extends TextWebSocketHandler {

    private final WebsocketRedisService websocketRedisService;

    private final ServerState serverState;

    /**
     * 接受客户端消息
     *
     * @param session
     *            session
     * @param message
     *            message
     * @throws IOException
     *             e
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        if (serverState.isShuttingDown()) {
            log.warn("Service is shutting down. Ignoring new messages.");
            return;
        }

        String sid = session.getId();
        // SocketMessage<?> socketMessage =
        // SocketUtil.formatSocketMessage(message.getPayload());
        SocketMessage msg = JsonUtils.parseObject(message.getPayload(), SocketMessage.class);
        assert msg != null;
        SocketChannelEnum channel = msg.getChannel();
        switch (channel) {
            default :
                log.warn(" 【websocket】 unknown message: {}, send to service ... ", message);
                // SocketMessage sb = JsonUtils.parseObject(message.getPayload(),
                // SocketMessage.class);
                TransferMessage tm = new TransferMessage();
                tm.setMessage(msg);
                String channelUsername = websocketRedisService.getUserBySessionId(sid);
                if (channelUsername != null) {
                    tm.setFromUser(channelUsername);
                }
                // 将消息透传给服务端，服务端需要注意消息的幂等处理
                websocketRedisService.sendWsToService(tm);
                break;
        }
    }

    /**
     * 连接建立后
     *
     * @param session
     *            session
     * @throws Exception
     *             e
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
        System.out.println("当前连接数:" + websocketRedisService.getConnectionCount());
        System.out.println("当前在线人数: " + websocketRedisService.getOnlineUserCount());
        System.out.println("当前内存中的用户: " + JsonUtils.toJsonString(websocketRedisService.getAllOnlineUsernames()));
    }

    /**
     * 连接关闭后
     *
     * @param session
     *            session
     * @param status
     *            status
     * @throws Exception
     *             e
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (serverState.isShuttingDown()) {
            log.warn("Service is shutting down. Skipping cleanup for closed connection.");
            return;
        }
        String loginId = (String) session.getAttributes().get(LOGIN_ID);
        log.info("【websocket消息】连接断开，loginId:[{}]", loginId);
        if (session.isOpen()) {
            try {
                String sid = session.getId();
                websocketRedisService.removeUserBySessionId(sid);
                // 注意删除顺序,先清除WSSession的Map
                SocketManagerCache.removeUserSession(sid);
                SocketManagerCache.onlineSessionMap.remove(sid);
                session.close();
            } catch (IOException e) {
                log.error("【websocket消息】连接断开异常，error", e);
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * 根据loginId 推送消息
     *
     * @param loginIds
     *            登陆用户Id集合
     * @param socketMessage
     *            消息对象
     */
    @SneakyThrows
    public void sendMessage(List<?> loginIds, SocketMessage socketMessage) {
        log.info(" 定向推送。推送用户范围:{}, message: {}", loginIds, JsonUtils.toJsonString(socketMessage));
        for (Object loginId : loginIds) {
            // 验证当前内存中【用户】是否存在
            boolean existsUsername = SocketManagerCache.onlineUserSessionIdMap.containsKey(Utils.getStringVal(loginId));
            if (existsUsername) {
                List<String> notifyUserSids = SocketManagerCache.onlineUserSessionIdMap.get(Utils.getStringVal(loginId));
                for (String notifyUserSid : notifyUserSids) {
                    // 验证当前内存中【session】是否存在
                    boolean existsUserSession = SocketManagerCache.onlineSessionMap.containsKey(notifyUserSid);
                    if (existsUserSession) {
                        WsSession wsSession = SocketManagerCache.onlineSessionMap.get(notifyUserSid);
                        wsSession.getSession().sendMessage(new TextMessage(SocketUtil.transferMessage(socketMessage)));
                    } else {
                        log.info(" websocket定向推送。message: {}。用户:{}推送失败", JsonUtils.toJsonString(socketMessage), loginId);
                    }
                }
            }
        }
    }

    /**
     * 根据loginId 推送消息
     *
     * @param socketMessage
     *            消息对象
     */
    @SneakyThrows
    public void sendMessageToAllUser(SocketMessage socketMessage) {
        log.info(" 全员推送。message: {}", JsonUtils.toJsonString(socketMessage));
        List<String> allOnlineUsernames = new ArrayList<>(SocketManagerCache.onlineUserSessionIdMap.keySet());
        for (String loginId : allOnlineUsernames) {
            List<String> notifyUserSids = SocketManagerCache.onlineUserSessionIdMap.get(loginId);
            for (String notifyUserSid : notifyUserSids) {
                boolean existsUserSession = SocketManagerCache.onlineSessionMap.containsKey(notifyUserSid);
                if (existsUserSession) {
                    WsSession wsSession = SocketManagerCache.onlineSessionMap.get(notifyUserSid);
                    wsSession.getSession().sendMessage(new TextMessage(SocketUtil.transferMessage(socketMessage)));
                } else {
                    log.info(" websocket全员推送。message: {}。用户:{}推送失败", JsonUtils.toJsonString(socketMessage), loginId);
                }
            }
        }
    }

    public void disconnectAll() {
        ConcurrentHashMap<String, WsSession> onlineSessionMap = SocketManagerCache.onlineSessionMap;
        for (Map.Entry<String, WsSession> sessionEntry : onlineSessionMap.entrySet()) {
            String sid = sessionEntry.getKey();
            WsSession wsSession = sessionEntry.getValue();
            // 清理 redis
            websocketRedisService.removeUserBySessionId(sid);
            // 断开websocket 连接 ...
            WebSocketSession session = wsSession.getSession();
            if (session != null) {
                try {
                    wsSession.getSession().close(CloseStatus.SERVICE_RESTARTED);
                    log.info(" 优雅退出，关闭 websocket 连接 ...");
                } catch (IOException e) {
                    log.error("【websocket消息】连接断开异常，error", e);
                    throw new IllegalArgumentException(e);
                }
            }
        }

    }

}
