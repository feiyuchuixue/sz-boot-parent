package com.sz.admin.system.pojo.vo.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DeptRoleInfoVO
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/17 09:40
 */
@Data
public class DeptRoleInfoVO {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "角色信ID")
    private String roleId;

    @Schema(description = "角色名")
    private String roleName;

}
