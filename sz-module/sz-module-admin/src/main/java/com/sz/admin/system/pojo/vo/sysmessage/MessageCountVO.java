package com.sz.admin.system.pojo.vo.sysmessage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * MessageCountVO - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/3/13
 */
@Data
@AllArgsConstructor
public class MessageCountVO {

    @Schema(description = "数量")
    private Long all;

    @Schema(description = "待办")
    private Long todo;

    @Schema(description = "消息")
    private Long msg;
}
