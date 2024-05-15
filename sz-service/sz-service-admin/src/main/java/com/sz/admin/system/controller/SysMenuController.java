package com.sz.admin.system.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.admin.system.pojo.dto.sysmenu.MenuPermissionDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuAddDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuListDTO;
import com.sz.admin.system.pojo.po.SysMenu;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.SysMenuVO;
import com.sz.admin.system.service.SysMenuService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */

@Tag(name =  "系统菜单管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys-menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "添加菜单")
    @SaCheckPermission(value = "sys.menu.create_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@Valid @RequestBody SysMenuAddDTO dto) {
        sysMenuService.create(dto);
        return ApiResult.success();
    }

    @SaIgnore
    @Operation(summary = "修改菜单")
    @SaCheckPermission(value = "sys.menu.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@Valid @RequestBody SysMenuAddDTO dto) {
        sysMenuService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "批量删除")
    @SaCheckPermission(value = "sys.menu.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        sysMenuService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.menu.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<List<SysMenuVO>> list(SysMenuListDTO dto) {
        return ApiResult.success(sysMenuService.menuList(dto));
    }

    @Operation(summary = "详情")
    @SaIgnore
    @GetMapping("{id}")
    public ApiResult<SysMenu> detail(@PathVariable String id) {
        return ApiResult.success(sysMenuService.detail(id));
    }

    @Operation(summary = "查询上级菜单")
    @SaIgnore
    @GetMapping("tree")
    public ApiResult<List<MenuTreeVO>> queryParentListTree(@RequestParam(required = false) String nodeId) {
        return ApiResult.success(sysMenuService.getSimpleMenuTree(nodeId));
    }

    @Operation(summary = "查询用户具有的菜单")
    @SaIgnore
    @GetMapping("/menu")
    public ApiResult queryMenuByUserId() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<SysMenuVO> menuList = sysMenuService.findMenuListByUserId(userId);
        return ApiResult.success(menuList);
    }

    @Operation(summary = "查询菜单按钮权限是否存在")
    @SaIgnore
    @GetMapping("/btn/exists")
    public ApiResult findBtnPermission(MenuPermissionDTO dto) {
        return ApiResult.success(sysMenuService.hasExistsPermissions(dto));
    }

    @Operation(summary = "查询全部按钮权限")
    @SaIgnore
    @GetMapping("/btn/permissions")
    public ApiResult<List<String>> findBtnPermission() {
        return ApiResult.success(sysMenuService.findPermission());
    }

    @Operation(summary = "菜单预览")
    @SaCheckPermission(value = "sys.menu.preview_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("sql/export")
    public ApiResult exportMenuSql(@RequestBody SelectIdsDTO dto, HttpServletResponse response) {
        return ApiResult.success(sysMenuService.exportMenuSql(dto));
    }

}
