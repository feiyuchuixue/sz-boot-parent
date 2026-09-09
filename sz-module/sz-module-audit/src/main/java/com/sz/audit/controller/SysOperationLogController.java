package com.sz.audit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.audit.pojo.dto.SysOperationLogListDTO;
import com.sz.audit.pojo.vo.SysOperationLogSummaryVO;
import com.sz.audit.pojo.vo.SysOperationLogVO;
import com.sz.audit.service.SysOperationLogService;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.logger.audit.OperationAuditIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作审计日志 Controller。
 */
@Tag(name = "操作审计日志")
@OperationAuditIgnore
@RestController
@RequestMapping("sys-operation-log")
@RequiredArgsConstructor
public class SysOperationLogController {

    private final SysOperationLogService sysOperationLogService;

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.operation.log.query_table")
    @GetMapping
    public ApiResult<PageResult<SysOperationLogVO>> list(SysOperationLogListDTO dto) {
        return ApiPageResult.success(sysOperationLogService.page(dto));
    }

    @Operation(summary = "轻量汇总")
    @SaCheckPermission(value = "sys.operation.log.query_table")
    @GetMapping("summary")
    public ApiResult<SysOperationLogSummaryVO> summary(SysOperationLogListDTO dto) {
        return ApiResult.success(sysOperationLogService.summary(dto));
    }

    @Operation(summary = "详情查询")
    @SaCheckPermission(value = "sys.operation.log.detail")
    @GetMapping("{id}")
    public ApiResult<SysOperationLogVO> detail(@PathVariable Long id) {
        return ApiResult.success(sysOperationLogService.detail(id));
    }
}
