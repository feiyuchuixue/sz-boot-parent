package com.sz.admin.system.pojo.dto.sysrolemenu;

import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author sz
 * @date 2023/8/28 15:49
 */
@Data
@Schema(description = "角色菜单变更")
public class SysRoleMenuDTO {

    @Schema(description = "菜单id数组")
    List<String> menuIds;

    @NotZero(message = "角色id不能为空")
    @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED)
    Long roleId;

}
