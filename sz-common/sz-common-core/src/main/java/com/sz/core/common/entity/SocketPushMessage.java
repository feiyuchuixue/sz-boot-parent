package com.sz.core.common.entity;

import com.sz.core.common.enums.SocketChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后端 → 前端的推送消息载体。
 *
 * <p>
 * 职责边界：
 * <ul>
 * <li>仅用于推送方向（服务端 → WebSocket 客户端）</li>
 * <li>{@code channel} 由 {@link SocketChannelEnum} 枚举值序列化为字符串，保持强类型约束</li>
 * <li>{@code data} 为可跨服务反序列化的业务数据，建议使用 String、Number、Boolean、List、Map
 * 等通用类型</li>
 * </ul>
 *
 * <p>
 * 使用静态工厂方法构建，简化调用：
 * 
 * <pre>{@code
 * // 无 data
 * SocketPushMessage.of(SocketChannelEnum.SYNC_DICT)
 *
 * // 有 data
 * SocketPushMessage.of(SocketChannelEnum.MESSAGE, Map.of("title", title, "content", content))
 * }</pre>
 *
 * @author sz
 * @since 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketPushMessage {

    @Schema(description = "推送频道")
    private String channel;

    @Schema(description = "业务数据（建议使用 String、Number、Boolean、List、Map 等可跨服务反序列化类型）")
    private Object data;

    /**
     * 构建无业务数据的推送消息（仅信令类频道，如 SYNC_DICT、KICK_OFF 等）。
     */
    public static SocketPushMessage of(SocketChannelEnum channel) {
        return new SocketPushMessage(channel.name(), null);
    }

    /**
     * 构建带业务数据的推送消息。跨 Redis Pub/Sub 推送时，data 不应使用业务模块私有 DTO， 否则独立 WebSocket
     * 服务可能因缺少该类而反序列化失败。
     */
    public static SocketPushMessage of(SocketChannelEnum channel, Object data) {
        return new SocketPushMessage(channel.name(), data);
    }

}
