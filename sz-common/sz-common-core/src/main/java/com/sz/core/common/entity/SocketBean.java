package com.sz.core.common.entity;

import com.sz.core.common.enums.MessageTransferScopeEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @date 2023/9/6 17:20
 */
@Data
public class SocketBean<T> {

    {
        data = (T) "";
        channel = SocketChannelEnum.DEFAULTS;
        scope = MessageTransferScopeEnum.SOCKET_CLIENT;
    }

    @Schema(description = "data")
    protected T data;

    @Schema(description = "通道类型")
    protected SocketChannelEnum channel;

    @Schema(description = "消息通知作用域")
    protected MessageTransferScopeEnum scope;

}
