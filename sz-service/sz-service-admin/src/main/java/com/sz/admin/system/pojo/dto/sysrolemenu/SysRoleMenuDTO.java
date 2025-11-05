package com.sz.admin.system.pojo.dto.sysrolemenu;

import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author sz
 * @since 2023/8/28 15:49
 */
@Data
@Schema(description = "角色菜单变更")
public class SysRoleMenuDTO {

    @NotZero(message = "角色id不能为空")
    @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;

    @Schema(description = "功能权限配置")
    private Menu menu;

    @Schema(description = "数据权限配置")
    private List<Scope> scope;

    @Data
    public static class Scope {

        @Schema(description = "数据权限范围")
        private String dataScope;

        @Schema(description = "部门id数组")
        private List<Long> deptIds;

        @Schema(description = "用户id数组")
        private List<Long> userIds;

        @Schema(description = "菜单id")
        private String menuId;

    }

    @Data
    public static class Menu {

        @Schema(description = "菜单id数组")
        private List<String> menuIds;
    }

}
