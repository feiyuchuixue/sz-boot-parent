package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptCreateDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptListDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptRoleDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptUpdateDTO;
import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.pojo.vo.sysdept.SysDeptLeaderVO;
import com.sz.admin.system.pojo.vo.sysdept.SysDeptRoleVO;
import com.sz.admin.system.pojo.vo.sysdept.SysDeptVO;
import com.sz.admin.system.service.SysDeptService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 部门表 Controller
 * </p>
 *
 * @author sz
 * @since 2024-03-20
 */
@Tag(name = "部门表")
@RestController
@RequestMapping("sys-dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "sys.dept.create", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult<Void> create(@RequestBody SysDeptCreateDTO dto) {
        sysDeptService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "sys.dept.update", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult<Void> update(@RequestBody SysDeptUpdateDTO dto) {
        sysDeptService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "sys.dept.remove", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysDeptService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.dept.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<List<SysDeptVO>> list(SysDeptListDTO dto) {
        return ApiResult.success(sysDeptService.list(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.dept.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<SysDeptVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysDeptService.detail(id));
    }

    @Operation(summary = "树形列表")
    @GetMapping("/tree")
    public ApiResult<List<DeptTreeVO>> tree(@Parameter(description = "需要排除的节点ID") @RequestParam(required = false) Integer excludeNodeId,
            @Parameter(description = "是否添加根节点") @RequestParam(required = false) Boolean appendRoot) {
        return ApiResult.success(sysDeptService.getDeptTree(excludeNodeId, appendRoot, false));
    }

    @Operation(summary = "负责人穿梭框-全部用户")
    @GetMapping("/leader")
    public ApiResult<SysDeptLeaderVO> leader() {
        return ApiResult.success(sysDeptService.findSysUserDeptLeader());
    }

    @Operation(summary = "部门角色信息查询-（穿梭框）")
    @SaCheckPermission(value = "sys.dept.role_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("role")
    public ApiResult<SysDeptRoleVO> findDeptRole(@NotZero @RequestParam Long deptId) {
        return ApiResult.success(sysDeptService.findSysDeptRole(deptId));
    }

    @Operation(summary = "部门角色信息修改 -（穿梭框）")
    @SaCheckPermission(value = "sys.dept.role_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("role")
    public ApiResult<Void> changeDeptRole(@Valid @RequestBody SysDeptRoleDTO dto) {
        sysDeptService.changeSysDeptRole(dto);
        return ApiResult.success();
    }

}
