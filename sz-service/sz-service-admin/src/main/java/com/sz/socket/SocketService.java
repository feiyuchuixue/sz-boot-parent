package com.sz.socket;

import com.sz.admin.system.pojo.dto.sysmessage.PayloadBody;
import com.sz.core.common.entity.SocketMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.MessageTransferScopeEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.JsonUtils;
import com.sz.redis.WebsocketRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocketService {

    private final WebsocketRedisService websocketRedisService;

    /**
     * 同步前端配置
     */
    public void syncFrontendConfig() {
        TransferMessage tm = new TransferMessage();
        tm.setToPushAll(true);
        SocketMessage sb = new SocketMessage();
        sb.setChannel(SocketChannelEnum.SYNC_FRONTEND_CONF);
        tm.setMessage(sb);
        websocketRedisService.sendServiceToWs(tm);
    }

    /**
     * 同步字典数据
     */
    public void syncDict() {
        TransferMessage tm = new TransferMessage();
        tm.setToPushAll(true);
        SocketMessage sb = new SocketMessage();
        sb.setChannel(SocketChannelEnum.SYNC_DICT);
        tm.setMessage(sb);
        websocketRedisService.sendServiceToWs(tm);
    }

    /**
     * 同步权限数据
     * 
     * @param userId
     *            用户id
     */
    public void syncPermission(Object userId) {
        TransferMessage tm = new TransferMessage();
        tm.setToPushAll(false);
        tm.setToUsers(List.of(userId));
        SocketMessage sb = new SocketMessage();
        sb.setChannel(SocketChannelEnum.SYNC_PERMISSIONS);
        tm.setMessage(sb);
        websocketRedisService.sendServiceToWs(tm);
    }

    /**
     * 强制（指定用户）下线
     * 
     * @param userId
     *            用户id
     */
    public void kickOff(Object userId) {
        TransferMessage tm = new TransferMessage();
        tm.setToUsers(Collections.singletonList(userId));
        SocketMessage sb = new SocketMessage();
        sb.setChannel(SocketChannelEnum.KICK_OFF);
        tm.setMessage(sb);
        websocketRedisService.sendServiceToWs(tm);
    }

    /**
     * 发送消息
     * 
     * @param body
     *            消息体
     * @param senderId
     *            发送者ID
     * @param receiverIds
     *            接收者ID列表
     */
    public void sendMessage(PayloadBody body, String senderId, List<Object> receiverIds) {
        SocketMessage message = SocketMessage.builder().data(JsonUtils.toJsonString(body)).channel(SocketChannelEnum.MESSAGE)
                .scope(MessageTransferScopeEnum.SOCKET_CLIENT).build();
        TransferMessage msg = TransferMessage.builder().message(message).fromUser(senderId).toPushAll(false).toUsers(receiverIds).build();
        websocketRedisService.sendServiceToWs(msg);
    }

    public void readMessage(String fromUserId, List<?> toUsers) {
        SocketMessage message = SocketMessage.builder().data(null).channel(SocketChannelEnum.READ).scope(MessageTransferScopeEnum.SOCKET_CLIENT).build();
        TransferMessage msg = new TransferMessage();
        msg.setMessage(message);
        msg.setFromUser(fromUserId);
        msg.setToPushAll(false);
        msg.setToUsers(toUsers);
        websocketRedisService.sendServiceToWs(msg);
    }

}
