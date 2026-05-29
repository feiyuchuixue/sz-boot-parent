package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.SysLoginLogListDTO;
import com.sz.admin.system.pojo.vo.SysLoginLogVO;
import com.sz.admin.system.service.SysLoginLogService;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.login.log.query_table")
    @GetMapping
    public ApiResult<PageResult<SysLoginLogVO>> list(SysLoginLogListDTO dto) {
        return ApiPageResult.success(sysLoginLogService.page(dto));
    }

}