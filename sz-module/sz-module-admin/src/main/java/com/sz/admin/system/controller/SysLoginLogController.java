package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.SysLoginLogCreateDTO;
import com.sz.admin.system.pojo.dto.SysLoginLogListDTO;
import com.sz.admin.system.pojo.dto.SysLoginLogUpdateDTO;
import com.sz.admin.system.pojo.vo.SysLoginLogVO;
import com.sz.admin.system.service.SysLoginLogService;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 登陆日志表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
@Tag(name = "登陆日志表")
@RestController
@RequestMapping("sys-login-log")
@RequiredArgsConstructor
public class SysLoginLogController {

    private final SysLoginLogService sysLoginLogService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "sys.login.log.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody SysLoginLogCreateDTO dto) {
        sysLoginLogService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "sys.login.log.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody SysLoginLogUpdateDTO dto) {
        sysLoginLogService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "sys.login.log.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysLoginLogService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.login.log.query_table")
    @GetMapping
    public ApiResult<PageResult<SysLoginLogVO>> list(SysLoginLogListDTO dto) {
        return ApiPageResult.success(sysLoginLogService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.login.log.query_table")
    @GetMapping("/{id}")
    public ApiResult<SysLoginLogVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysLoginLogService.detail(id));
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "sys.login.log.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody SysLoginLogListDTO dto, HttpServletResponse response) {
        sysLoginLogService.exportExcel(dto, response);
    }
}