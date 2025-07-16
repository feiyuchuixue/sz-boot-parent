package com.sz.admin.system.pojo.vo.common;

import com.sz.core.common.service.Treeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DepartmentVO - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/6
 */
@Schema(description = "部门信息返回VO")
@Data
public class DepartmentVO implements Treeable<DepartmentVO> {

    @Schema(description = "部门ID")
    private Object id;

    @Schema(description = "父级部门ID")
    private Object pid;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "子级部门列表")
    private List<DepartmentVO> children = new ArrayList<>();

    @Schema(description = "层级")
    private Long deep;

    @Schema(description = "排序")
    private Long sort;

}
