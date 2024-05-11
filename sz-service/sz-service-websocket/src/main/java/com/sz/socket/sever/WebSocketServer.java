package com.sz.socket.sever;

import com.sz.core.common.entity.SocketBean;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.entity.WsSession;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.JsonUtils;
import com.sz.core.util.SocketUtil;
import com.sz.redis.WebsocketRedisService;
import com.sz.socket.cache.SocketManagerCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sz.socket.cache.SocketManagerCache.LOGIN_ID;

@Component
@Slf4j
public class WebSocketServer extends TextWebSocketHandler {
    private static WebsocketRedisService websocketRedisService;

    @Autowired
    public void setRedisService(WebsocketRedisService websocketRedisService) {
        WebSocketServer.websocketRedisService = websocketRedisService;
    }

    /**
     * 接受客户端消息
     *
     * @param session session
     * @param message message
     * @throws IOException e
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("handleTextMessage 0..");
        String sid = session.getId();
        SocketBean socketBean = SocketUtil.formatSocketMessage(message.getPayload());
        SocketChannelEnum channel = socketBean.getChannel();
        // TODO channel 处理
        switch (channel) {
            default:
                log.warn(" 【websocket】 unknown message: {}, send to service ... ", message);
                SocketBean sb = JsonUtils.parseObject(message.getPayload(), SocketBean.class);
                TransferMessage tm = new TransferMessage();
                tm.setMessage(sb);
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
     * @param session session
     * @throws Exception e
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sid = session.getId();
        WsSession wsSession = new WsSession(sid, "", session);
        String username = (String) session.getAttributes().get(LOGIN_ID);
        SocketManagerCache.onlineSessionMap.put(sid, wsSession);
        SocketManagerCache.addOnlineSid(username, sid);
        websocketRedisService.addUserToOnlineChat(sid, username);
        System.out.println("当前连接数:" + websocketRedisService.getConnectionCount());
        System.out.println("当前在线人数: " + websocketRedisService.getOnlineUserCount());
        System.out.println("当前内存中的用户: " + JsonUtils.toJsonString(websocketRedisService.getAllOnlineUsernames()));
    }

    /**
     * 连接关闭后
     *
     * @param session session
     * @param status  status
     * @throws Exception e
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get(LOGIN_ID);
        log.info("【websocket消息】连接断开，username:[{}]", username);
        if (session != null && session.isOpen()) {
            try {
                String sid = session.getId();
                websocketRedisService.removeUserBySessionId(sid);
                // 注意删除顺序,先清除WSSession的Map
                SocketManagerCache.removeUserSession(sid);
                SocketManagerCache.onlineSessionMap.remove(sid);
                session.close();
            } catch (IOException e) {
                log.error("【websocket消息】连接断开异常，error: {}", e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据username 推送消息
     *
     * @param usernames
     * @param socketBean
     */
    @SneakyThrows
    public void sendMessage(List<String> usernames, SocketBean socketBean) {
        log.info(" 定向推送。推送用户范围:{}, message: {}", usernames, JsonUtils.toJsonString(socketBean));
        for (String username : usernames) {
            // 验证当前内存中【用户】是否存在
            boolean existsUsername = SocketManagerCache.onlineUserSessionIdMap.containsKey(username);
            if (existsUsername) {
                List<String> notifyUserSids = SocketManagerCache.onlineUserSessionIdMap.get(username);
                for (String notifyUserSid : notifyUserSids) {
                    // 验证当前内存中【session】是否存在
                    boolean existsUserSession = SocketManagerCache.onlineSessionMap.containsKey(notifyUserSid);
                    if (existsUserSession) {
                        WsSession wsSession = SocketManagerCache.onlineSessionMap.get(notifyUserSid);
                        wsSession.getSession().sendMessage(new TextMessage(SocketUtil.transferMessage(socketBean)));
                    } else {
                        log.info(" websocket定向推送。message: {}。用户:{}推送失败",JsonUtils.toJsonString(socketBean), username);
                    }
                }
            }
        }
    }

    /**
     * 根据username 推送消息
     *
     * @param socketBean
     */
    @SneakyThrows
    public void sendMessageToAllUser(SocketBean socketBean) {
        log.info(" 全员推送。message: {}", JsonUtils.toJsonString(socketBean));
        List<String> allOnlineUsernames = new ArrayList<>(SocketManagerCache.onlineUserSessionIdMap.keySet());
        for (String username : allOnlineUsernames) {
            List<String> notifyUserSids = SocketManagerCache.onlineUserSessionIdMap.get(username);
            for (String notifyUserSid : notifyUserSids) {
                boolean existsUserSession = SocketManagerCache.onlineSessionMap.containsKey(notifyUserSid);
                if (existsUserSession) {
                    WsSession wsSession = SocketManagerCache.onlineSessionMap.get(notifyUserSid);
                    wsSession.getSession().sendMessage(new TextMessage(SocketUtil.transferMessage(socketBean)));
                } else {
                    log.info(" websocket全员推送。message: {}。用户:{}推送失败", JsonUtils.toJsonString(socketBean), username);
                }
            }
        }
    }

}

