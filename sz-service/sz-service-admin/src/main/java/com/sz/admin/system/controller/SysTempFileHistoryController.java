package com.sz.admin.system.controller;

import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryListDTO;
import com.sz.admin.system.pojo.po.SysTempFileHistory;
import com.sz.admin.system.service.SysTempFileHistoryService;
import com.sz.core.common.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 模版文件历史表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-14
 */
@Tag(name = "模版文件历史表")
@RestController
@RequestMapping("sys-temp-file-history")
@RequiredArgsConstructor
public class SysTempFileHistoryController {

    private final SysTempFileHistoryService sysTempFileHistoryService;

    @Operation(summary = "模板文件历史查询")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @GetMapping("history")
    public ApiResult<PageResult<SysTempFileHistory>> list(SysTempFileHistoryListDTO dto) {
        return ApiPageResult.success(sysTempFileHistoryService.historyList(dto));
    }

}