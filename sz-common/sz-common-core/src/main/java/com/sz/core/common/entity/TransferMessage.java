package com.sz.core.common.entity;

import com.sz.core.common.enums.MessageTransferScopeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 微服务间传递的 WebSocket 消息信封。
 *
 * <p>
 * 职责边界：
 * <ul>
 * <li>仅用于后端微服务之间通过 Redis Pub/Sub 传递，不直接发送给前端</li>
 * <li>{@code message} 为最终推送给客户端的载体 {@link SocketPushMessage}</li>
 * <li>{@code scope} 决定消息路由目标（由此处统一管理，不再散落在 SocketPushMessage 中）</li>
 * </ul>
 *
 * <p>
 * 推荐使用 {@link com.sz.core.util.SocketUtil} 工厂方法构建，减少样板代码：
 * 
 * <pre>{@code
 * // 全员广播
 * SocketUtil.broadcast(SocketPushMessage.of(SocketChannelEnum.SYNC_DICT));
 *
 * // 定向推送
 * SocketUtil.toUsers(SocketPushMessage.of(SocketChannelEnum.KICK_OFF), List.of("1"));
 * }</pre>
 *
 * @author sz
 * @since 2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferMessage {

    @Schema(description = "推送给客户端的消息体")
    private SocketPushMessage message;

    /**
     * 消息接收人（loginId 字符串列表）。
     */
    @Schema(description = "消息接收人（loginId 列表）")
    @Builder.Default
    private List<String> toUsers = new ArrayList<>();

    @Schema(description = "消息发送人（loginId）")
    private String fromUser;

    @Schema(description = "是否推送给全部在线用户")
    @Builder.Default
    private boolean toPushAll = false;

    /**
     * 消息路由范围：决定消息由哪一层处理。 默认 {@link MessageTransferScopeEnum#SOCKET_CLIENT}，即最终推送至
     * WebSocket 客户端。
     */
    @Schema(description = "消息路由范围")
    @Builder.Default
    private MessageTransferScopeEnum scope = MessageTransferScopeEnum.SOCKET_CLIENT;

}
