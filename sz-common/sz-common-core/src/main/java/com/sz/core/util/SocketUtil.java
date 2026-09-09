package com.sz.core.util;

import com.sz.core.common.entity.SocketPushMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.MessageTransferScopeEnum;

import java.util.List;

/**
 * WebSocket 消息构建工具。
 *
 * <p>
 * 提供语义化的静态工厂方法，消除样板代码。典型用法：
 * 
 * <pre>{@code
 * // 全员广播（无 data）
 * SocketUtil.broadcast(SocketPushMessage.of(SocketChannelEnum.SYNC_DICT));
 *
 * // 全员广播（带 data）
 * SocketUtil.broadcast(SocketPushMessage.of(SocketChannelEnum.UPGRADE_CHANNEL, "系统升级中"));
 *
 * // 定向推送
 * SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.KICK_OFF), List.of("1", "2"));
 *
 * // 定向推送（带发送人）
 * SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.MESSAGE, body), toUsers, senderId);
 * }</pre>
 *
 * @author sz
 * @since 2.0
 */
public class SocketUtil {

    private SocketUtil() {
    }

    /**
     * 构建全员广播的 {@link TransferMessage}。
     */
    public static TransferMessage broadcast(SocketPushMessage message) {
        return TransferMessage.builder().message(message).toPushAll(true).scope(MessageTransferScopeEnum.SOCKET_CLIENT).build();
    }

    /**
     * 构建定向推送的 {@link TransferMessage}（无发送人）。
     */
    public static TransferMessage toUsers(SocketPushMessage message, List<String> toUsers) {
        return TransferMessage.builder().message(message).toUsers(toUsers).toPushAll(false).scope(MessageTransferScopeEnum.SOCKET_CLIENT).build();
    }

    /**
     * 构建定向推送的 {@link TransferMessage}（带发送人）。
     */
    public static TransferMessage toUsers(SocketPushMessage message, List<String> toUsers, String fromUser) {
        return TransferMessage.builder().message(message).toUsers(toUsers).fromUser(fromUser).toPushAll(false).scope(MessageTransferScopeEnum.SOCKET_CLIENT)
                .build();
    }

    /**
     * 将 {@link SocketPushMessage} 序列化为 JSON 字符串，供 WebSocket 文本帧发送。
     */
    public static String serialize(SocketPushMessage message) {
        return JsonUtils.toJsonString(message);
    }

}
