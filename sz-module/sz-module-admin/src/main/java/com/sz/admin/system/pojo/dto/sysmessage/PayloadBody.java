package com.sz.admin.system.pojo.dto.sysmessage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * PayloadBody - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/4/21
 */
@Data
public class PayloadBody {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;
}
