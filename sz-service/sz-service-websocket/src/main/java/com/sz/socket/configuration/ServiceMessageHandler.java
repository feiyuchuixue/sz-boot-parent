package com.sz.socket.configuration;

import com.sz.core.common.entity.SocketPushMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.util.JsonUtils;
import com.sz.redis.handler.ServiceToWsMsgHandler;
import com.sz.socket.sever.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 在业务层接收订阅的消息，并结合业务进行处理
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceMessageHandler implements ServiceToWsMsgHandler {

    private final WebSocketServer webSocketServer;

    @Override
    public void handleTransferMessage(TransferMessage tm) {
        log.info(" sz-service-websocket [service-to-ws] tm = {}", JsonUtils.toJsonString(tm));
        SocketPushMessage tmMessage = tm.getMessage();
        if (tmMessage == null) {
            log.warn("【websocket】service-to-ws 消息体为空，已忽略");
            return;
        }
        switch (tm.getScope()) {
            case SOCKET_CLIENT : // 通知到 socket 客户端，即浏览器、移动端等
                if (tm.isToPushAll()) {
                    webSocketServer.sendMessageToAllUser(tmMessage);
                } else {
                    webSocketServer.sendMessage(tm.getToUsers(), tmMessage);
                }
                break;
            // SERVER / SOCKET_SERVER 当前 ws 服务无需处理，由业务侧或其他 handler 接管
            default :
                break;
        }
    }
}
