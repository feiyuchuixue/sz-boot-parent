package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.sysdatarole.SysDataRoleCreateDTO;
import com.sz.admin.system.pojo.dto.sysdatarole.SysDataRoleListDTO;
import com.sz.admin.system.pojo.dto.sysdatarole.SysDataRoleUpdateDTO;
import com.sz.admin.system.pojo.vo.sysdatarole.SysDataRoleVO;
import com.sz.admin.system.service.SysDataRoleService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 数据权限管理 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-09
 */
@Tag(name = "数据权限管理")
@RestController
@RequestMapping("sys-data-role")
@RequiredArgsConstructor
public class SysDataRoleController {

    private final SysDataRoleService sysDataRoleService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "sys.data.role.create", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@RequestBody SysDataRoleCreateDTO dto) {
        sysDataRoleService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "sys.data.role.update", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@RequestBody SysDataRoleUpdateDTO dto) {
        sysDataRoleService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "sys.data.role.remove", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        sysDataRoleService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.data.role.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysDataRoleVO>> list(SysDataRoleListDTO dto) {
        return ApiPageResult.success(sysDataRoleService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.data.role.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<SysDataRoleVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysDataRoleService.detail(id));
    }

    @Operation(summary = "数据权限角色菜单信息查询")
    @SaCheckPermission(value = "sys.data.role.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/menu")
    public ApiResult findRoleMenuByRoleId() {
        return ApiPageResult.success(sysDataRoleService.queryDataRoleMenu());
    }

}
