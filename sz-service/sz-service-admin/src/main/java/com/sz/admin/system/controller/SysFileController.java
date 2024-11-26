package com.sz.admin.system.controller;

import com.sz.admin.system.pojo.dto.sysfile.SysFileListDTO;
import com.sz.admin.system.pojo.po.SysFile;
import com.sz.admin.system.service.SysFileService;
import com.sz.core.common.annotation.DebounceIgnore;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.oss.UploadResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 公共文件 前端控制器
 * </p>
 *
 * @author sz
 * @since 2023-08-31
 */
@Tag(name = "系统公共文件管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys-file")
public class SysFileController {

    private final SysFileService sysFileService;

    @Operation(summary = "列表查询")
    @GetMapping
    public ApiPageResult<PageResult<SysFile>> list(SysFileListDTO dto) {
        return ApiPageResult.success(sysFileService.fileList(dto));
    }

    @DebounceIgnore
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public ApiResult<UploadResult> upload(@RequestParam MultipartFile file, @RequestParam(value = "dirTag") String dirTag) {
        return ApiResult.success(sysFileService.uploadFile(file, dirTag));
    }

}
