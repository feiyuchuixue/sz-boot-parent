package com.sz.admin.system.pojo.vo.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DeptOptionsVo - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/28
 */
@Data
@Schema(description = "部门选项")
public class DeptOptionsVO {

    @Schema(description = "部门Id")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

}
