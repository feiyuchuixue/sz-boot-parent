package com.sz.admin.system.pojo.dto.sysrole;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @date 2023/8/24 15:28
 */
@Data
@Schema(description = "角色查询")
public class SysRoleListDTO extends PageQuery {

    @Schema(description = "角色名称")
    private String roleName;

}
