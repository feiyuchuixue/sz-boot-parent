package com.sz.admin.system.pojo.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * RoleVO - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/6
 */
@Data
public class RoleVO {

    @Schema(description = "角色ID")
    private Object id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色编码")
    private String permissions;

}
