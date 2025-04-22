package com.sz.core.common.entity;

import com.sz.core.common.enums.MessageTransferScopeEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sz
 * @since 2023/9/6 17:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessage {

    @Schema(description = "data")
    protected String data;

    @Schema(description = "通道类型")
    protected SocketChannelEnum channel = SocketChannelEnum.DEFAULTS;

    @Schema(description = "消息通知作用域")
    protected MessageTransferScopeEnum scope = MessageTransferScopeEnum.SOCKET_CLIENT;

}
