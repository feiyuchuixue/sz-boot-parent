package com.sz.admin.system.pojo.vo.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DeptRoleInfoVO
 * 
 * @author sz
 * @since 2025/7/17 09:40
 * @version 1.0
 */
@Data
public class DeptRoleInfoVO {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "角色信息,多个逗号分隔")
    private String roleInfos;

    @Schema(description = "角色ID")
    private String roleIds;

}
