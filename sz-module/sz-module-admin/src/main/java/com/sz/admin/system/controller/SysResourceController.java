package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.sz.admin.system.pojo.dto.sysresource.SysResourceListDTO;
import com.sz.admin.system.pojo.vo.sysresource.SysResourceVO;
import com.sz.admin.system.service.SysResourceService;
import com.sz.core.common.annotation.DebounceIgnore;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.logger.audit.OperationAudit;
import com.sz.logger.audit.OperationType;
import com.sz.resource.model.ResourceUploadResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Tag(name = "统一资源管理")
@Slf4j
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class SysResourceController {

    private final SysResourceService sysResourceService;

    @Operation(summary = "资源分页查询")
    @GetMapping
    public ApiPageResult<PageResult<SysResourceVO>> page(SysResourceListDTO dto) {
        return ApiPageResult.success(sysResourceService.page(dto));
    }

    /**
     * 上传资源文件
     *
     * @param sceneCode
     *            场景编码，如 sso.provider.logo
     * @param namingKey
     *            命名用业务标识（命名规则为 BIZ_KEY 时必填，如 providerKey）； UUID
     *            规则时可不传。<b>注意：与路径分层策略（pathSegments）无关</b>
     * @param file
     *            上传的文件
     * @return 上传结果，包含 objectKey、accessUrl 和 resourceId
     */
    @DebounceIgnore
    @Operation(summary = "上传资源文件", description = "根据 sceneCode 自动选择存储驱动，并写入 sys_resource 审计记录。返回 objectKey（存入业务表）、accessUrl（前端预览用）和 resourceId。")
    @OperationAudit(operationType = OperationType.IMPORT)
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ApiResult<ResourceUploadResult> upload(@Parameter(description = "场景编码，如 sso.provider.logo") @RequestParam("sceneCode") String sceneCode,
            @Parameter(description = "命名用业务标识，BIZ_KEY 命名规则时必填；与路径分层策略无关") @RequestParam(value = "bizKey", required = false) String namingKey,
            @Parameter(description = "路径分段，逗号分割，BIZ/BIZ_DATE 策略时生效，如 \"userId,dept\"") @RequestParam(value = "pathSegments", required = false) String pathSegments,
            @Parameter(description = "上传的文件") @RequestPart("file") MultipartFile file) throws IOException {
        String[] segments = (pathSegments != null && !pathSegments.isBlank()) ? pathSegments.split(",") : new String[0];
        ResourceUploadResult result = sysResourceService.upload(sceneCode, namingKey, file, segments);
        return ApiResult.success(result);
    }

    /**
     * 访问资源文件（支持多层子目录，流式响应）- 建议谨慎使用，更推荐nginx或oss服务
     *
     * <p>
     * URL 格式：{@code /resource/file/{sceneDir}/**}
     * <ul>
     * <li>sceneDir 对应场景 base-url 的最后一段，用于反查场景配置</li>
     * <li>{@code **} 部分为文件在场景目录下的相对路径（含子目录和文件名）</li>
     * </ul>
     *
     * <p>
     * 示例：
     *
     * <pre>
     *   GET /resource/file/providers/github.svg
     *     → objectKey = providers/github.svg
     *
     *   GET /resource/file/avatars/1/20260403/abc.png
     *     → objectKey = avatars/1/20260403/abc.png
     * </pre>
     *
     * @param sceneDir
     *            场景目录名，如 providers、avatars
     * @param request
     *            原始请求（用于提取完整路径中的 subPath）
     * @return 流式文件响应
     */
    /**
     * 批量上传资源文件（富文本编辑器专用）
     *
     * @param sceneCode
     *            场景编码，如 teacher.richtext
     * @param pathSegments
     *            路径分段，逗号分割，BIZ/BIZ_DATE 策略时生效
     * @param request
     *            原始请求（从 multipart 中提取所有文件）
     * @return 上传结果列表，每项包含 objectKey、accessUrl 和 resourceId
     */
    @DebounceIgnore
    @Operation(summary = "批量上传资源文件", description = "支持同时上传多个文件，用于富文本编辑器等场景。每个文件独立处理，返回结果列表。")
    @OperationAudit(operationType = OperationType.IMPORT)
    @PostMapping(value = "/batchUpload", consumes = "multipart/form-data")
    public ApiResult<List<ResourceUploadResult>> batchUpload(@Parameter(description = "场景编码，如 teacher.richtext") @RequestParam("sceneCode") String sceneCode,
            @Parameter(description = "路径分段，逗号分割，BIZ/BIZ_DATE 策略时生效") @RequestParam(value = "pathSegments", required = false) String pathSegments,
            HttpServletRequest request) throws IOException {
        String[] segments = (pathSegments != null && !pathSegments.isBlank()) ? pathSegments.split(",") : new String[0];
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = new ArrayList<>();
        Iterator<String> fileNames = multipartRequest.getFileNames();
        while (fileNames.hasNext()) {
            files.addAll(multipartRequest.getFiles(fileNames.next()));
        }
        List<ResourceUploadResult> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(sysResourceService.upload(sceneCode, null, file, segments));
        }
        return ApiResult.success(results);
    }

    @Profile({"dev", "local", "preview"}) // 生产环境尽量使用nginx或oss
    @SaIgnore
    @Operation(summary = "访问资源文件", description = "无需登录。支持子目录，流式响应，URL 格式：/resource/file/{sceneDir}/**")
    @GetMapping("/file/{sceneDir}/**")
    public ResponseEntity<StreamingResponseBody> serveFile(@Parameter(description = "场景目录名，如 providers、avatars") @PathVariable String sceneDir,
            HttpServletRequest request) {
        return sysResourceService.findServeFile(sceneDir, request);
    }
}
