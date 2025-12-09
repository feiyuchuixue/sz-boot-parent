package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.sz.admin.system.pojo.dto.sysfile.SysFileListDTO;
import com.sz.admin.system.pojo.po.SysFile;
import com.sz.admin.system.service.SysFileService;
import com.sz.core.common.annotation.DebounceIgnore;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.UploadResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
@Slf4j
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

    @SaIgnore
    @Operation(summary = "批量上传文件")
    @PostMapping("/batchUpload")
    public ApiResult<List<UploadResult>> batchUpload(HttpServletRequest request, @RequestParam(value = "dirTag") String dirTag) {
        List<UploadResult> urlList = new ArrayList<>();
        try {
            // 从 request 中获取 MultipartFile 数组
            MultipartFile[] files = getMultipartFiles(request);
            for (MultipartFile file : files) {
                UploadResult uploadResult = sysFileService.uploadFile(file, dirTag);
                urlList.add(uploadResult);
            }
        } catch (Exception e) {
            log.error("文件上传失败：", e);
        }
        return ApiResult.success(urlList);
    }

    private MultipartFile[] getMultipartFiles(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        Iterator<String> fileNames = multipartRequest.getFileNames();
        while (fileNames.hasNext()) {
            String fileName = fileNames.next();
            multipartFiles.addAll(multipartRequest.getFiles(fileName));
        }
        return multipartFiles.toArray(new MultipartFile[0]);
    }

}
