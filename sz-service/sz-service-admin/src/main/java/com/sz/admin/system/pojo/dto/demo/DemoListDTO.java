package com.sz.admin.system.pojo.dto.demo;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Demo查询DTO
 *
 * @author sz
 * @since 2025-01-14
 */
@Data
@Schema(description = "Demo查询DTO")
public class DemoListDTO extends PageQuery {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "类型")
    private String type;
}