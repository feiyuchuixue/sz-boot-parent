package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleCreateDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleListDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleUpdateDTO;
import com.sz.admin.system.pojo.dto.sysrolemenu.SysRoleMenuDTO;
import com.sz.admin.system.pojo.po.SysRole;
import com.sz.admin.system.service.SysRoleMenuService;
import com.sz.admin.system.service.SysRoleService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统角色表 前端控制器
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/sys-role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    private final SysRoleMenuService sysRoleMenuService;

    @Operation(summary = "角色新增")
    @SaCheckPermission(value = "sys.role.create_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@Valid @RequestBody SysRoleCreateDTO dto) {
        sysRoleService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "角色修改")
    @SaCheckPermission(value = "sys.role.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@Valid @RequestBody SysRoleUpdateDTO dto) {
        sysRoleService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除、批量删除")
    @SaCheckPermission(value = "sys.role.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        sysRoleService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.role.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysRole>> listPage(SysRoleListDTO dto) {
        return ApiPageResult.success(sysRoleService.list(dto));
    }

    @Operation(summary = "角色菜单配置")
    @SaCheckPermission(value = {"sys.role.setting_btn", "sys.role.update_btn"}, mode = SaMode.AND, orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("/menu")
    public ApiResult changeRoleMenu(@RequestBody SysRoleMenuDTO dto) {
        sysRoleMenuService.change(dto);
        return ApiPageResult.success();
    }

    @Operation(summary = "角色菜单信息查询")
    @SaCheckPermission(value = {"sys.role.setting_btn", "sys.role.update_btn"}, mode = SaMode.AND, orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/menu")
    public ApiResult findRoleMenuByRoleId(@NotZero @RequestParam Long roleId) {
        return ApiPageResult.success(sysRoleMenuService.queryRoleMenu(roleId));
    }

}
