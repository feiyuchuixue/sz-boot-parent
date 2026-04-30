package com.sz.admin.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysResourceMapper;
import com.sz.admin.system.pojo.po.SysResource;
import com.sz.admin.system.service.SysResourceService;
import com.sz.resource.config.ResourceProperties;
import com.sz.resource.config.ResourceSceneConfig;
import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import com.sz.resource.model.ResourceUploadResult;
import com.sz.resource.service.ResourceService;
import com.sz.resource.util.PathSanitizer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SysResourceServiceImpl extends ServiceImpl<SysResourceMapper, SysResource> implements SysResourceService {

    private final ResourceService resourceService;

    private final ResourceProperties resourceProperties;

    private final SysResourceMapper sysResourceMapper;

    /**
     * 上传文件并写入 sys_resource 审计记录
     *
     * @param sceneCode
     *            场景编码，如 sso.provider.logo
     * @param namingKey
     *            命名用业务标识（BIZ_KEY 命名规则时必填，其他规则传 null）， 与路径分层策略（pathSegments）无关
     * @param file
     *            上传的文件
     * @param pathSegments
     *            路径分段（可选），用于 BIZ_ONLY / BIZ_DATE 策略，每段对应一级子目录
     * @return 上传结果（含 resourceId）
     * @throws IOException
     *             文件存储失败
     */
    @Override
    public ResourceUploadResult upload(String sceneCode, String namingKey, MultipartFile file, String... pathSegments) throws IOException {
        // 1. 委托底层 ResourceService 完成校验、存储、URL 生成
        ResourceUploadResult result = resourceService.upload(sceneCode, namingKey, file, pathSegments);

        // 2. 写入 sys_resource 审计记录
        ResourceSceneConfig scene = resourceProperties.getScene(sceneCode);
        SysResource resource = new SysResource();
        resource.setSceneCode(sceneCode);
        resource.setObjectKey(result.getObjectKey());
        resource.setOriginName(result.getOriginName());
        resource.setSize(result.getSize());
        resource.setContentType(result.getContentType());
        resource.setStorageType(scene.getType() == StorageTypeEnum.OSS ? "OSS" : "LOCAL");
        resource.setETag(result.getETag());
        resource.setBizKey(namingKey);
        resource.setDelFlag("F");
        sysResourceMapper.insert(resource);
        log.info("[ResourceManage] sys_resource 写入成功，id={} scene={} objectKey={}", resource.getId(), sceneCode, result.getObjectKey());

        // 3. 将 resourceId 回写到结果
        result.setResourceId(resource.getId());
        return result;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> findServeFile(String sceneDir, HttpServletRequest request) {

        // 1. 安全校验 sceneDir
        if (containsPathTraversal(sceneDir)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 2. 从请求路径中提取 subPath（sceneDir 之后的部分）
        String subPath = extractSubPath(request, sceneDir);
        if (subPath == null || containsPathTraversal(subPath)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 3. 反查匹配的 DIRECT 场景
        String sceneCode = resolveSceneCode(sceneDir);
        if (sceneCode == null) {
            log.warn("[Resource] 未找到匹配 sceneDir={} 的 DIRECT 场景配置", sceneDir);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 4. 组装 objectKey = sceneDir + "/" + subPath
        String objectKey = sceneDir + "/" + subPath;

        // 5. 流式写出文件内容，避免将整个文件加载进内存
        MediaType mediaType = resolveMediaType(subPath);
        StreamingResponseBody body = outputStream -> {
            try (InputStream in = resourceService.readStream(sceneCode, objectKey)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.flush();
            } catch (IOException e) {
                log.error("[Resource] 流式读取文件失败，objectKey={}", objectKey, e);
                throw e;
            }
        };

        return ResponseEntity.ok().contentType(mediaType).body(body);
    }

    /**
     * 从请求路径中提取 sceneDir 之后的 subPath，并做 URL decode
     *
     * <pre>
     *   requestURI = /api/admin/resource/file/logo/user/logo/20260430/%E5%BE%AE%E4%BF%A1.png
     *   sceneDir   = logo
     *   → subPath  = 微信.png  （decode 后与磁盘实际文件名一致）
     * </pre>
     *
     * <p>
     * 浏览器访问含非 ASCII 字符（如汉字）的文件时，URL 中的文件名段会被自动 percent-encode（如 "微信.png" →
     * "%E5%BE%AE%E4%BF%A1.png"）。{@link HttpServletRequest#getRequestURI()} 返回的是
     * encode 后的值，若不做 decode 直接拼 objectKey，将找不到磁盘上以原始汉字命名的文件。
     * </p>
     */
    private String extractSubPath(HttpServletRequest request, String sceneDir) {
        String uri = request.getRequestURI();
        // 找到 "/{sceneDir}/" 的位置，截取之后的部分作为 subPath
        String marker = "/" + sceneDir + "/";
        int idx = uri.indexOf(marker);
        if (idx < 0) {
            // 没有子路径，说明请求的就是 sceneDir 本身（不合法，必须包含文件名）
            return null;
        }
        String rawSubPath = uri.substring(idx + marker.length());
        // URL decode：还原 percent-encoded 字符（含汉字等非 ASCII），使其与磁盘实际文件名一致
        try {
            return URLDecoder.decode(rawSubPath, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // decode 失败（如含非法 % 序列），视为不合法路径，返回 null 由上层返回 400
            log.warn("[Resource] subPath URL decode 失败，rawSubPath={}", rawSubPath);
            return null;
        }
    }

    /**
     * 根据 sceneDir 反查匹配的 DIRECT 场景 code 匹配规则：比较场景 base-url 的最后一段路径是否等于 sceneDir
     */
    private String resolveSceneCode(String sceneDir) {
        return resourceProperties.getSceneMap().entrySet().stream().filter(e -> e.getValue().getServeMode() == ServeModeEnum.DIRECT).filter(e -> {
            String baseUrl = e.getValue().getBaseUrl();
            if (baseUrl == null || baseUrl.isBlank())
                return false;
            String trimmed = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            int lastSlash = trimmed.lastIndexOf('/');
            String lastSegment = lastSlash >= 0 ? trimmed.substring(lastSlash + 1) : trimmed;
            return sceneDir.equals(lastSegment);
        }).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    /**
     * 根据文件扩展名解析 MediaType
     */
    private MediaType resolveMediaType(String filename) {
        if (filename == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".svg"))
            return MediaType.valueOf("image/svg+xml");
        if (lower.endsWith(".png"))
            return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
            return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".webp"))
            return MediaType.valueOf("image/webp");
        if (lower.endsWith(".gif"))
            return MediaType.IMAGE_GIF;
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    /**
     * 检查路径段是否包含穿越攻击特征，委托给 {@link PathSanitizer} 统一处理
     *
     * <p>
     * 同时检查原始值和 URL decode 后的值，防止 {@code %2e%2e} 等编码变体绕过。
     */
    private boolean containsPathTraversal(String segment) {
        return !PathSanitizer.validate(segment, PathSanitizer.Mode.HTTP_PATH);
    }

}
