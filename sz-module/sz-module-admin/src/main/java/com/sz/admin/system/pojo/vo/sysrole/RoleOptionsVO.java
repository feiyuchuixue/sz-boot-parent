package com.sz.admin.system.pojo.vo.sysrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * RoleOptions - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/29
 */
@Data
public class RoleOptionsVO {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String permissions;

}
