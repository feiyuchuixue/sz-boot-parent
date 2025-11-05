package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;

@Schema(description = "用户的数据权限范围汇总信息")
@Data
public class RoleMenuScopeVO {

    @Schema(description = "sys_menu_id （菜单表）")
    private String menuId;

    @Schema(description = "数据权限范围")
    private String dataScopeCd;

    @Schema(description = "自定义数据权限范围，当dataScopeCd为1006005时使用")
    private CustomScope customScope;

    @Data
    public static class CustomScope {

        @Schema(description = "用户ID")
        private Collection<Long> userIds;

        @Schema(description = "部门ID")
        private Collection<Long> deptIds;

    }

}
