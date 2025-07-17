package com.sz.admin.system.pojo.dto.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Demo创建DTO
 *
 * @author sz
 * @since 2025-01-14
 */
@Data
@Schema(description = "Demo创建DTO")
public class DemoCreateDTO {

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "排序")
    private Integer sort;
}