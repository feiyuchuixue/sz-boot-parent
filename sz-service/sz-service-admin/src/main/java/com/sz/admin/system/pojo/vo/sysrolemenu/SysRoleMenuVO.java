package com.sz.admin.system.pojo.vo.sysrolemenu;

import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysuser.UserOptionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sz
 * @since 2023/8/29 9:22
 */
@Data
@Schema(description = "角色菜单详情")
public class SysRoleMenuVO {

    @Schema(description = "选中的菜单id")
    private List<String> selectMenuIds = new ArrayList<>();

    @Schema(description = "菜单列表")
    private List<MenuTreeVO> menuLists;

    @Schema(description = "部门列表")
    private List<DeptTreeVO> deptLists;

    @Schema(description = "用户列表")
    private List<UserOptionVO> userLists;

    @Schema(description = "数据权限配置")
    private List<Scope> scope;

    @Data
    public static class Scope {

        @Schema(description = "菜单id")
        private String menuId;

        @Schema(description = "数据权限范围")
        private String dataScope;

        @Schema(description = "部门id数组")
        private List<Long> deptIds;

        @Schema(description = "用户id数组")
        private List<Long> userIds;

    }

}
