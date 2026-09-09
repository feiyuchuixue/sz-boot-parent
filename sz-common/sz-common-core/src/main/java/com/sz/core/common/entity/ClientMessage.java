package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端 → 后端的接收消息载体。
 *
 * <p>
 * 职责边界：
 * <ul>
 * <li>仅用于接收方向（WebSocket 客户端 → 服务端）</li>
 * <li>{@code channel} 为字符串，避免枚举反序列化对未知值（如 PING）抛异常或返回 null</li>
 * <li>后端业务逻辑中用 {@link com.sz.core.common.enums.SocketChannelEnum} 做 equals
 * 判断</li>
 * <li>{@code data} 为 Object，Jackson 反序列化为 {@code LinkedHashMap} 或基本类型，
 * 业务层按需强转或再次反序列化</li>
 * </ul>
 *
 * @author sz
 * @since 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientMessage {

    @Schema(description = "频道标识（字符串，对应 SocketChannelEnum.name() 或自定义值如 PING）")
    private String channel;

    @Schema(description = "业务数据")
    private Object data;

}
