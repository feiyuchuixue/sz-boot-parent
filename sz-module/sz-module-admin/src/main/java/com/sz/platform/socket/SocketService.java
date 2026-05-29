package com.sz.platform.socket;

import com.sz.admin.system.pojo.dto.sysmessage.PayloadBody;
import com.sz.core.common.entity.SocketPushMessage;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.SocketUtil;
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
     * 同步前端配置（全员广播）
     */
    public void syncFrontendConfig() {
        websocketRedisService.sendServiceToWs(SocketUtil.broadcast(SocketPushMessage.of(SocketChannelEnum.SYNC_FRONTEND_CONF)));
    }

    /**
     * 同步字典数据（全员广播）
     */
    public void syncDict() {
        websocketRedisService.sendServiceToWs(SocketUtil.broadcast(SocketPushMessage.of(SocketChannelEnum.SYNC_DICT)));
    }

    /**
     * 同步权限数据（定向推送）
     *
     * @param userId
     *            用户id
     */
    public void syncPermission(Long userId) {
        websocketRedisService.sendServiceToWs(SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.SYNC_PERMISSIONS), List.of(String.valueOf(userId))));
    }

    /**
     * 强制（指定用户）下线
     *
     * @param userId
     *            用户id
     */
    public void kickOff(Long userId) {
        websocketRedisService
                .sendServiceToWs(SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.KICK_OFF), Collections.singletonList(String.valueOf(userId))));
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
    public void sendMessage(PayloadBody body, Long senderId, List<Long> receiverIds) {
        List<String> toUsers = receiverIds == null ? Collections.emptyList() : receiverIds.stream().map(String::valueOf).toList();
        websocketRedisService.sendServiceToWs(SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.MESSAGE, body), toUsers, String.valueOf(senderId)));
    }

    public void readMessage(Long fromUserId, List<Long> toUsers) {
        List<String> normalized = toUsers == null ? Collections.emptyList() : toUsers.stream().map(String::valueOf).toList();
        websocketRedisService.sendServiceToWs(SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.READ), normalized, String.valueOf(fromUserId)));
    }

}
