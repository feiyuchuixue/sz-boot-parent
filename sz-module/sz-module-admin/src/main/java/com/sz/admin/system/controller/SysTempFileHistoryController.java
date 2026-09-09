package com.sz.admin.system.controller;

import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryListDTO;
import com.sz.admin.system.pojo.po.SysTempFileHistory;
import com.sz.admin.system.service.SysTempFileHistoryService;
import com.sz.resource.service.ResourceDownloadService;
import com.sz.core.common.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * <p>
 * 模板文件历史 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-14
 */
@Tag(name = "模板文件历史")
@RestController
@RequestMapping("sys-temp-file-history")
@RequiredArgsConstructor
public class SysTempFileHistoryController {

    private final SysTempFileHistoryService sysTempFileHistoryService;

    private final ResourceDownloadService resourceDownloadService;

    @Operation(summary = "模板文件历史查询")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @GetMapping("history")
    public ApiResult<PageResult<SysTempFileHistory>> list(SysTempFileHistoryListDTO dto) {
        return ApiPageResult.success(sysTempFileHistoryService.historyList(dto));
    }

    @Operation(summary = "下载模板历史文件")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @PostMapping("/{id}/resources/{resourceId}/download")
    public ResponseEntity<StreamingResponseBody> downloadResource(@PathVariable Long id, @PathVariable Long resourceId) {
        sysTempFileHistoryService.validateResourceAccess(id, resourceId);
        return resourceDownloadService.download(resourceId);
    }

    @Operation(summary = "预览模板历史文件")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @PostMapping("/{id}/resources/{resourceId}/preview")
    public ResponseEntity<StreamingResponseBody> previewResource(@PathVariable Long id, @PathVariable Long resourceId) {
        sysTempFileHistoryService.validateResourceAccess(id, resourceId);
        return resourceDownloadService.preview(resourceId);
    }

}
