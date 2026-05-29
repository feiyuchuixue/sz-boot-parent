package com.sz.admin.system.pojo.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SelectorVO - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/6
 */
@Schema(description = "多维选择器返回VO")
@Data
public class SelectorVO {

    @Schema(description = "类型", allowableValues = {"user", "role", "department"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "结果数据")
    private Object data;

}
