package com.sz.core.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sz.core.common.enums.SocketChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @since 2023/9/6 10:17
 */
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
@Data
public class SocketResult<T> extends SocketMessage<T> {

    @Schema(description = "状态码。200代表成功；非200失败")
    protected int status = 200;

    public static <T> SocketResult<T> success() {
        SocketResult<T> socketResult = new SocketResult<>();
        socketResult.data = null;
        return socketResult;
    }

    public static <T> SocketResult<T> success(SocketChannelEnum channel, T data) {
        SocketResult<T> socketResult = new SocketResult<>();
        socketResult.channel = channel;
        socketResult.data = data;
        return socketResult;
    }

    public static <T> SocketResult<T> fail(SocketChannelEnum channel, T data) {
        SocketResult<T> socketResult = new SocketResult<>();
        socketResult.status = 0;
        socketResult.channel = channel;
        socketResult.data = data;
        return socketResult;
    }

}
