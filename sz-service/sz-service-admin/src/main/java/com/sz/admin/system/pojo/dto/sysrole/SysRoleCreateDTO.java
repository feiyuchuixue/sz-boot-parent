package com.sz.admin.system.pojo.dto.sysrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @date 2023/8/24 15:28
 */
@Data
@Schema(description = "角色添加")
public class SysRoleCreateDTO {

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "标识")
    private String permissions;

}
