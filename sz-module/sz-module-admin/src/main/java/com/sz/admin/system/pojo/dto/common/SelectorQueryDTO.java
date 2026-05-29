package com.sz.admin.system.pojo.dto.common;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SelectorQueryDTO - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/5
 */
@Schema(description = "多维选择器查询DTO")
@Data
public class SelectorQueryDTO extends PageQuery {

    @Schema(description = "类型", allowableValues = {"user", "role", "department"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "父级维度 ID，用于 user 时代表 deptId，department 时代表上级部门 ID")
    private Object parentId;

}
