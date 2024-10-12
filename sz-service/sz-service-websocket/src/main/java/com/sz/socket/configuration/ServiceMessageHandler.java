package com.sz.socket.configuration;

import com.sz.core.common.entity.SocketBean;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.JsonUtils;
import com.sz.redis.handler.ServiceToWsMsgHandler;
import com.sz.socket.sever.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 在业务层接收订阅额消息，并结合业务进行处理
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceMessageHandler implements ServiceToWsMsgHandler {

    private final WebSocketServer webSocketServer;

    @Override
    public void handleTransferMessage(TransferMessage tm) {
        log.info(" sz-service-websocket [service-to-ws] tm = " + JsonUtils.toJsonString(tm));
        SocketBean tmMessage = tm.getMessage();
        SocketChannelEnum channel = tmMessage.getChannel();
        switch (tmMessage.getScope()) {
            case SERVER : // 通知到后台服务端
                if (SocketChannelEnum.CLOSE == channel) {
                    // todo ...
                }
                break;
            case SOCKET_CLIENT : // 通知到socket客户端，即浏览器、移动端等
                // 推送给全体用户
                if (tm.isToPushAll()) {
                    webSocketServer.sendMessageToAllUser(tm.getMessage());
                    // 推送给指定用户
                } else {
                    webSocketServer.sendMessage(tm.getToUsers(), tm.getMessage());
                }
                break;
            case SOCKET_SERVER :
                // todo something ..
                break;
            default :
                break;
        }
    }
}
