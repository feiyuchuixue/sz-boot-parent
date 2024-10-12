package com.sz.core.common.entity;

import com.sz.core.common.enums.SocketChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @date 2023/9/6 10:17
 */
@Data
public class SocketResult<T> extends SocketBean {

    @Schema(description = "状态码。200代表成功；非200失败")
    protected int status;

    {
        status = 200;
    }

    public static <T> SocketResult<T> success() {
        SocketResult socketResult = new SocketResult();
        socketResult.data = (T) "";
        return socketResult;
    }

    public static <T> SocketResult<T> success(SocketChannelEnum channel, T data) {
        SocketResult socketResult = new SocketResult();
        socketResult.channel = channel;
        socketResult.data = (data != null) ? data : (T) "";
        return socketResult;
    }

    public static <T> SocketResult<T> success(SocketChannelEnum channel, T data, String message) {
        SocketResult<T> socketResult = new SocketResult<>();
        socketResult.channel = channel;
        socketResult.data = (data != null) ? data : (T) "";
        return socketResult;
    }

    public static <T> SocketResult<T> fail(SocketChannelEnum channel, T data) {
        SocketResult socketResult = new SocketResult();
        socketResult.status = 0;
        socketResult.channel = channel;
        socketResult.data = (data != null) ? data : (T) "";
        return socketResult;
    }

}
