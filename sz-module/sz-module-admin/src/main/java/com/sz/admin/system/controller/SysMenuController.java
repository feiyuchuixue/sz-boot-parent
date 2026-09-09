package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.admin.system.pojo.dto.scriptexport.ScriptExportDTO;
import com.sz.admin.system.pojo.dto.sysmenu.MenuPermissionDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuCreateDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuListDTO;
import com.sz.admin.system.pojo.po.SysMenu;
import com.sz.admin.system.pojo.vo.scriptexport.ScriptExportVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuPermissionVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.SysMenuVO;
import com.sz.admin.system.service.SysMenuService;
import com.sz.admin.system.service.SysPermissionService;
import com.sz.core.common.annotation.DebounceIgnore;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.logger.audit.OperationAudit;
import com.sz.logger.audit.OperationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */

@Tag(name = "系统菜单管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys-menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    private final SysPermissionService sysPermissionService;

    @Operation(summary = "新增菜单")
    @SaCheckPermission(value = "sys.menu.create_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult<Void> create(@Valid @RequestBody SysMenuCreateDTO dto) {
        sysMenuService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改菜单")
    @OperationAudit(operationType = OperationType.UPDATE, bizId = "#dto.id")
    @SaCheckPermission(value = "sys.menu.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult<Void> update(@Valid @RequestBody SysMenuCreateDTO dto) {
        sysMenuService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除菜单")
    @SaCheckPermission(value = "sys.menu.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysMenuService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "查询菜单列表")
    @SaCheckPermission(value = "sys.menu.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<List<SysMenuVO>> list(SysMenuListDTO dto) {
        return ApiResult.success(sysMenuService.menuList(dto));
    }

    @Operation(summary = "查询菜单详情")
    @SaCheckPermission(value = "sys.menu.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("{id}")
    public ApiResult<SysMenu> detail(@PathVariable Long id) {
        return ApiResult.success(sysMenuService.detail(id));
    }

    @Operation(summary = "查询上级菜单树")
    @GetMapping("tree")
    public ApiResult<List<MenuTreeVO>> queryParentListTree(@RequestParam(required = false) Long nodeId) {
        return ApiResult.success(sysMenuService.getSimpleMenuTree(nodeId));
    }

    @DebounceIgnore
    @Operation(summary = "查询当前用户菜单")
    @GetMapping("/menu")
    public ApiResult<List<SysMenuVO>> queryMenuByUserId() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<SysMenuVO> menuList = sysMenuService.findMenuListByUserId(userId);
        return ApiResult.success(menuList);
    }

    @Operation(summary = "校验按钮权限编码")
    @GetMapping("/btn/exists")
    public ApiResult<MenuPermissionVO> findBtnPermission(MenuPermissionDTO dto) {
        return ApiResult.success(sysMenuService.hasExistsPermissions(dto));
    }

    @DebounceIgnore
    @Operation(summary = "查询全部按钮权限")
    @GetMapping("/btn/permissions")
    public ApiResult<List<String>> findBtnPermission() {
        return ApiResult.success(sysMenuService.findPermission());
    }

    @Operation(summary = "导出菜单 Liquibase脚本")
    @OperationAudit(operationType = OperationType.EXPORT, bizId = "#dto.ids")
    @SaCheckPermission(value = "sys.menu.sql_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("sql/export")
    public ApiResult<String> exportMenuSql(@RequestBody SelectIdsDTO dto) {
        return ApiResult.success(sysMenuService.exportMenuSql(dto));
    }

    @Operation(summary = "导出菜单 SQL脚本")
    @OperationAudit(operationType = OperationType.EXPORT, bizId = "#dto.ids")
    @SaCheckPermission(value = "sys.menu.sql_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("script/export")
    public ApiResult<ScriptExportVO> exportMenuScript(@RequestBody ScriptExportDTO dto) {
        return ApiResult.success(sysMenuService.exportMenuScript(dto));
    }

    @DebounceIgnore
    @Operation(summary = "查询当前用户角色", description = "如果用户是超级管理员（user_tag_cd='1001002'），输出 'admin'；否则输出用户的角色id")
    @GetMapping("/user/roles")
    public ApiResult<List<String>> findUserRoles() {
        Set<String> roles = sysPermissionService.getRoles(StpUtil.getLoginIdAsLong());
        return ApiResult.success(new ArrayList<>(roles));
    }

    @Operation(summary = "修改数据权限状态")
    @OperationAudit(operationType = OperationType.UPDATE, bizId = "#id")
    @SaCheckPermission(value = "sys.menu.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("/datarole/change/{id}")
    public ApiResult<Void> changeDataRole(@PathVariable Long id) {
        sysMenuService.changeMenuDataScope(id);
        return ApiResult.success();
    }
}
