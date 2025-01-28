package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sz
 * @since 2023/9/8 16:10
 */
@Data
public class TransferMessage<T> {

    @Schema(description = "消息体bean")
    private SocketMessage<T> message;

    @Schema(description = "消息接收人")
    private List<String> toUsers = new ArrayList<>();

    @Schema(description = "消息发送人")
    private String fromUser;

    @Schema(description = "是否通知全部用户")
    private boolean toPushAll = false;

}
