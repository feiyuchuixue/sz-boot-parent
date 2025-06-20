package com.sz.ssoadmin.ssouser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.ssoadmin.ssouser.service.SsoUserService;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserCreateDTO;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserUpdateDTO;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserListDTO;
import com.sz.ssoadmin.ssouser.pojo.vo.SsoUserVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 平台用户表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
@Tag(name = "平台用户表")
@RestController
@RequestMapping("sso-user")
@RequiredArgsConstructor
public class SsoUserController {

    private final SsoUserService ssoUserService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "sso.user.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody SsoUserCreateDTO dto) {
        ssoUserService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "sso.user.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody SsoUserUpdateDTO dto) {
        ssoUserService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "sso.user.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        ssoUserService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sso.user.query_table")
    @GetMapping
    public ApiResult<PageResult<SsoUserVO>> list(SsoUserListDTO dto) {
        return ApiPageResult.success(ssoUserService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sso.user.query_table")
    @GetMapping("/{id}")
    public ApiResult<SsoUserVO> detail(@PathVariable Object id) {
        return ApiResult.success(ssoUserService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({@Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),})
    @SaCheckPermission(value = "sso.user.import")
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        ssoUserService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "sso.user.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody SsoUserListDTO dto, HttpServletResponse response) {
        ssoUserService.exportExcel(dto, response);
    }

    @Operation(summary = "重置密码")
    @SaCheckPermission(value = "sso.user.resetPwd")
    @PutMapping("reset/pwd/{id}")
    public ApiResult<Void> resetPwd(@PathVariable Long id) {
        ssoUserService.resetPassword(id);
        return ApiResult.success();
    }

}