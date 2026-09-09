package com.sz.platform.socket;

import com.sz.admin.system.pojo.dto.sysmessage.PayloadBody;
import com.sz.core.common.entity.SocketPushMessage;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.SocketUtil;
import com.sz.redis.WebsocketRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        if (userId == null) {
            return;
        }
        websocketRedisService.sendServiceToWs(SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.SYNC_PERMISSIONS), List.of(String.valueOf(userId))));
    }

    /**
     * 强制（指定用户）下线
     *
     * @param userId
     *            用户id
     */
    public void kickOff(Long userId) {
        if (userId == null) {
            return;
        }
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
        List<String> toUsers = normalizeUserIds(receiverIds);
        websocketRedisService.sendServiceToWs(
                SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.MESSAGE, toMessagePayload(body)), toUsers, normalizeUserId(senderId)));
    }

    public void readMessage(Long fromUserId, List<Long> toUsers) {
        List<String> normalized = normalizeUserIds(toUsers);
        websocketRedisService.sendServiceToWs(SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.READ), normalized, normalizeUserId(fromUserId)));
    }

    private static List<String> normalizeUserIds(List<Long> userIds) {
        return userIds == null ? Collections.emptyList() : userIds.stream().filter(Objects::nonNull).map(String::valueOf).toList();
    }

    private static String normalizeUserId(Long userId) {
        return userId == null ? null : String.valueOf(userId);
    }

    private static Map<String, Object> toMessagePayload(PayloadBody body) {
        if (body == null) {
            return null;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", body.getTitle());
        payload.put("content", body.getContent());
        return payload;
    }

}
