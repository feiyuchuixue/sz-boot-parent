package com.sz.admin.system.pojo.vo.sysdatarole;

import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysuser.UserOptionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SysDataRole返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-09
 */
@Data
@Schema(description = "SysDataRole返回vo")
public class SysDataRoleMenuVO {

    @Schema(description = "菜单列表")
    private List<MenuTreeVO> menuLists;

    @Schema(description = "部门列表")
    private List<DeptTreeVO> deptLists;

    @Schema(description = "用户列表")
    private List<UserOptionVO> userOptions;

}