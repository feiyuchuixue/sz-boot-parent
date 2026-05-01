package com.sz.resource.service;

import com.sz.resource.config.ResourceProperties;
import com.sz.resource.config.ResourceSceneConfig;
import com.sz.resource.config.ResourceSecurityDefaults;
import com.sz.resource.driver.LocalResourceStorageDriver;
import com.sz.resource.driver.OssResourceStorageDriver;
import com.sz.resource.driver.ResourceStorageDriver;
import com.sz.resource.enums.NamingRuleEnum;
import com.sz.resource.enums.PathStrategyEnum;
import com.sz.resource.enums.ResourceResponseEnum;
import com.sz.resource.enums.StorageTypeEnum;
import com.sz.resource.model.ResourceRef;
import com.sz.resource.model.ResourceUploadResult;
import com.sz.resource.spi.ResourceSceneProvider;
import com.sz.resource.spi.ResourceSecurityPolicyProvider;
import com.sz.resource.util.PathSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 统一资源管理核心服务
 *
 * <p>
 * 提供以下核心能力：
 * <ul>
 * <li>{@link #upload} - 上传文件，根据场景配置选择存储驱动和路径分层策略</li>
 * <li>{@link #resolveUrl} - 根据场景配置和 objectKey 生成完整可访问 URL</li>
 * <li>{@link #normalizeObjectKey} - 防御性方法，将前端误传的完整 URL 还原为 objectKey</li>
 * <li>{@link #readBytes} - 读取文件字节（用于代理访问接口）</li>
 * <li>{@link #readStream} - 读取文件流（用于代理访问接口）</li>
 * </ul>
 *
 * <h3>安全校验流程（三层模型）</h3>
 * <ol>
 * <li>文件名清洗（去路径分隔符、控制字符、长度限制）</li>
 * <li>硬编码黑名单拒绝（{@link ResourceSecurityDefaults#BLOCKED_EXTS}）</li>
 * <li>双重扩展名检测</li>
 * <li>全局白名单过滤（{@link ResourceSecurityPolicyProvider#getAllowedExts()}）</li>
 * <li>场景白名单过滤（{@code scene.exts}，非空时才检查）</li>
 * <li>MIME 类型校验</li>
 * <li>文件大小校验（取全局和场景中较小值）</li>
 * </ol>
 *
 * <h3>objectKey 与物理路径关系（LOCAL 模式）</h3>
 *
 * <pre>
 *   objectKey  = {path}/{subDirs}/{filename}   （不含 root 前缀）
 *   物理路径   = root + "/" + objectKey
 * </pre>
 */
@Slf4j
@Service
public class ResourceService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final ResourceProperties resourceProperties;

    private final LocalResourceStorageDriver localDriver;

    private final OssResourceStorageDriver ossDriver;

    private final ResourceSceneProvider sceneProvider;

    private final ResourceSecurityPolicyProvider securityProvider;

    public ResourceService(ResourceProperties resourceProperties, LocalResourceStorageDriver localDriver,
            ObjectProvider<OssResourceStorageDriver> ossDriverProvider, ResourceSceneProvider sceneProvider, ResourceSecurityPolicyProvider securityProvider) {
        this.resourceProperties = resourceProperties;
        this.localDriver = localDriver;
        this.ossDriver = ossDriverProvider.getIfAvailable();
        this.sceneProvider = sceneProvider;
        this.securityProvider = securityProvider;
    }

    /**
     * 上传文件
     *
     * <p>
     * 根据 sceneCode 查找场景配置，自动选择存储驱动（LOCAL / OSS）， 按命名规则和路径分层策略生成
     * objectKey，存储文件，返回上传结果。
     *
     * <h3>pathSegments 说明</h3>
     * <p>
     * 当场景配置 pathStrategy 为 {@code BIZ} 或 {@code BIZ_DATE} 时， pathSegments
     * 中的每个元素对应一级子目录。
     *
     * @param sceneCode
     *            场景编码，如 "user.avatar"
     * @param namingKey
     *            命名用业务标识，仅 naming=BIZ_KEY 时用作文件名，其他规则传 null
     * @param file
     *            上传的文件
     * @param pathSegments
     *            路径分段（可选），每个元素对应一级子目录
     * @return 上传结果，包含 objectKey 和 accessUrl
     * @throws IOException
     *             文件存储失败
     */
    public ResourceUploadResult upload(String sceneCode, String namingKey, MultipartFile file, String... pathSegments) throws IOException {
        ResourceSceneConfig scene = getScene(sceneCode);

        // 1. 安全校验（三层模型）
        validateFile(file, scene);

        // 2. 生成 objectKey
        String objectKey = buildObjectKey(file, namingKey, scene, pathSegments);

        // 3. 选择驱动并上传
        ResourceStorageDriver driver = resolveDriver(scene);

        // ORIGINAL 命名规则：优先保持原文件名，冲突时追加毫秒时间戳（Local / OSS 均通过 driver.exists 统一判断）
        if (scene.getNaming() == NamingRuleEnum.ORIGINAL && driver.exists(objectKey, scene)) {
            objectKey = appendTimestamp(objectKey);
        }
        String eTag = driver.upload(file, objectKey, scene);
        log.info("[Resource] 上传成功 scene={} objectKey={} eTag={}", sceneCode, objectKey, eTag);

        // 4. 生成访问 URL
        String accessUrl = resolveUrl(sceneCode, objectKey);

        return ResourceUploadResult.builder().objectKey(objectKey).originName(file.getOriginalFilename()).size(file.getSize())
                .contentType(file.getContentType()).accessUrl(accessUrl).eTag(eTag).build();
    }

    /**
     * 根据场景配置和 objectKey 生成完整可访问 URL
     *
     * <pre>
     * DIRECT + LOCAL：  baseUrl + "/" + objectKey 中 basePath 之后的相对路径
     * DIRECT + OSS：    OSS 公开访问 URL（由 OssClient 构建）
     * PRESIGNED：       自动生成 S3 Presigned URL（仅 OSS），调用方无需感知
     * TOKEN：           返回 null，由上层 ResourceAccessService.generateTempAccessUrl 按需生成
     * </pre>
     *
     * @param sceneCode
     *            场景编码
     * @param objectKey
     *            存储键，如 "avatars/1/20260403/abc.png"
     * @return 完整可访问 URL，TOKEN 场景返回 null
     */
    public String resolveUrl(String sceneCode, String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        ResourceSceneConfig scene = getScene(sceneCode);
        return switch (scene.getServeMode()) {
            case DIRECT -> buildPublicUrl(objectKey, scene);
            case PRESIGNED -> buildPresignedUrl(objectKey, scene);
            case TOKEN -> null;
        };
    }

    /**
     * 将 objectKey 或完整 URL 规范化为纯 objectKey（防御入库污染）
     *
     * @param sceneCode
     *            场景编码
     * @param rawValue
     *            前端提交的原始值
     * @return 规范化后的 objectKey
     */
    public String normalizeObjectKey(String sceneCode, String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return rawValue;
        }
        if (!rawValue.startsWith("http://") && !rawValue.startsWith("https://")) {
            return rawValue;
        }
        ResourceSceneConfig scene = getScene(sceneCode);
        String base = scene.getBaseUrl();
        if (base != null && !base.isBlank() && rawValue.startsWith(base)) {
            // URL = base-url + "/" + relPath（relPath 不含 path/prefix）
            // 还原完整 objectKey = path/prefix + relPath
            String trimmedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
            String relPath = rawValue.substring(trimmedBase.length());
            if (relPath.startsWith("/")) {
                relPath = relPath.substring(1);
            }
            int queryIdx = relPath.indexOf('?');
            if (queryIdx >= 0) {
                relPath = relPath.substring(0, queryIdx);
            }
            // 补回 path/prefix 前缀，还原为完整 objectKey
            String basePath = resolveSceneBasePath(scene);
            String objectKey = (basePath == null || basePath.isBlank()) ? relPath : joinPath(basePath, relPath);
            PathSanitizer.validate(objectKey, PathSanitizer.Mode.OBJECT_KEY);
            log.warn("[Resource] 检测到完整 URL 入库，已自动规范化：{} → {}", rawValue, objectKey);
            return objectKey;
        }
        throw ResourceResponseEnum.OBJECT_KEY_NORMALIZE_FAILED.message("无法将值规范化为 objectKey，请勿提交完整 URL：" + rawValue).newException();
    }

    /**
     * 批量填充 accessUrl（依赖 ResourceRef 中存储的 sceneCode）
     *
     * <p>
     * 典型用法：查询 VO 返回前调用此方法，自动将 objectKey 转换为可访问 URL。
     *
     * @param refs
     *            文件引用列表，元素的 sceneCode 和 objectKey 均不为空时才填充
     */
    public void fillAccessUrl(List<ResourceRef> refs) {
        if (refs == null)
            return;
        refs.stream().filter(r -> r.getObjectKey() != null && r.getSceneCode() != null)
                .forEach(r -> r.setAccessUrl(resolveUrl(r.getSceneCode(), r.getObjectKey())));
    }

    /**
     * 读取文件字节内容（用于代理访问接口）
     */
    public byte[] readBytes(String sceneCode, String objectKey) throws IOException {
        ResourceSceneConfig scene = getScene(sceneCode);
        return resolveDriver(scene).read(objectKey, scene);
    }

    /**
     * 读取文件输入流（用于代理访问接口）
     */
    public InputStream readStream(String sceneCode, String objectKey) throws IOException {
        ResourceSceneConfig scene = getScene(sceneCode);
        return resolveDriver(scene).readStream(objectKey, scene);
    }

    /**
     * 通过 SPI 获取场景配置，找不到则抛异常
     */
    private ResourceSceneConfig getScene(String sceneCode) {
        ResourceSceneConfig scene = sceneProvider.getScene(sceneCode);
        ResourceResponseEnum.SCENE_NOT_FOUND.message("未找到资源场景配置，sceneCode=" + sceneCode).assertNull(scene);
        return scene;
    }

    /**
     * 完整的文件校验流程
     */
    private void validateFile(MultipartFile file, ResourceSceneConfig scene) {
        ResourceResponseEnum.FILE_EMPTY.assertTrue(file == null || file.isEmpty());

        // 1. 文件名清洗
        String originalFilename = sanitizeOriginalFilename(file);
        String ext = extractExtension(originalFilename).toLowerCase();

        // 2. 硬编码黑名单
        ResourceResponseEnum.BLOCKED_EXT.message("文件类型 ." + ext + " 在黑名单中，禁止上传（场景：" + scene.getCode() + "）")
                .assertTrue(ResourceSecurityDefaults.BLOCKED_EXTS.contains(ext));

        // 3. 双重扩展名检测
        checkDoubleExtension(originalFilename);

        // 4. 全局白名单
        checkGlobalAllowedExts(ext, scene);

        // 5. 场景白名单
        checkSceneAllowedExts(ext, scene);

        // 6. MIME 类型校验
        checkMimeType(file.getContentType(), scene);

        // 7. 文件大小校验
        checkFileSize(file.getSize(), scene);
    }

    /**
     * 清洗原始文件名：去路径分隔符、控制字符、长度限制（≤255）
     */
    private String sanitizeOriginalFilename(MultipartFile file) {
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
        // 去掉路径分隔符（某些浏览器会传完整路径）
        int lastSlash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (lastSlash >= 0) {
            name = name.substring(lastSlash + 1);
        }
        // 去掉控制字符
        name = name.replaceAll("[\\x00-\\x1f\\x7f]", "");
        // 长度限制
        if (name.length() > 255) {
            String ext = extractExtension(name);
            name = name.substring(0, 245) + "." + ext;
        }
        if (name.isBlank()) {
            name = "file";
        }
        return name;
    }

    /**
     * 双重扩展名检测：遍历所有 "." 分段，任一段命中黑名单则拒绝
     * <p>
     * 防止 malware.php.jpg 类攻击
     */
    private void checkDoubleExtension(String filename) {
        String[] parts = filename.split("\\.");
        // 从第二段开始检查（第一段是文件名主体）
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].toLowerCase();
            ResourceResponseEnum.DOUBLE_EXT_ATTACK.message("检测到双重扩展名攻击，文件名中包含危险扩展名 ." + part + "：" + filename)
                    .assertTrue(ResourceSecurityDefaults.BLOCKED_EXTS.contains(part));
        }
    }

    /**
     * 全局白名单检查
     */
    private void checkGlobalAllowedExts(String ext, ResourceSceneConfig scene) {
        Set<String> globalExts = securityProvider.getAllowedExts();
        boolean uncheck = globalExts != null && !globalExts.isEmpty() && !globalExts.contains(ext);
        ResourceResponseEnum.GLOBAL_EXT_NOT_ALLOWED.message("文件类型 ." + ext + " 不在全局白名单中（场景：" + scene.getCode() + "）").assertTrue(uncheck);
    }

    /**
     * 场景白名单检查（非空时才检查，进一步收窄全局白名单）
     */
    private void checkSceneAllowedExts(String ext, ResourceSceneConfig scene) {
        boolean uncheck = scene.getExts() != null && !scene.getExts().isEmpty() && !scene.getExts().contains(ext);
        ResourceResponseEnum.SCENE_EXT_NOT_ALLOWED.message("不支持的文件类型：" + ext + "，场景 " + scene.getCode() + " 仅支持：" + scene.getExts()).assertTrue(uncheck);
    }

    /**
     * MIME 类型校验：优先使用场景级 MIME 白名单，未配则使用全局 MIME 白名单
     */
    private void checkMimeType(String contentType, ResourceSceneConfig scene) {
        String mime = Optional.ofNullable(contentType).orElse("").toLowerCase();
        if (mime.isBlank()) {
            return; // 无 MIME 信息时不校验（部分客户端不传）
        }
        // 场景级 MIME 白名单优先
        if (scene.getMimes() != null && !scene.getMimes().isEmpty()) {
            if (!scene.getMimes().contains(mime)) {
                ResourceResponseEnum.MIME_NOT_ALLOWED.message("不支持的 MIME 类型：" + mime + "（场景：" + scene.getCode() + "）").assertFalse(true);
            }
            return;
        }
        // 全局 MIME 白名单
        Set<String> globalMimes = securityProvider.getAllowedMimeTypes();
        boolean uncheck = globalMimes != null && !globalMimes.isEmpty() && !globalMimes.contains(mime);
        ResourceResponseEnum.MIME_NOT_ALLOWED.message("MIME 类型 " + mime + " 不在全局白名单中（场景：" + scene.getCode() + "）").assertTrue(uncheck);

    }

    /**
     * 文件大小校验：取全局和场景中较小值
     */
    private void checkFileSize(long fileSize, ResourceSceneConfig scene) {
        long globalMax = securityProvider.getMaxSizeBytes();
        long sceneMax = (scene.getMaxSize() > 0) ? scene.getMaxSize() * 1024 * 1024 : Long.MAX_VALUE;
        long effectiveMax = Math.min(globalMax, sceneMax);
        boolean overSizeCheck = fileSize > effectiveMax;
        long maxMb = effectiveMax / (1024 * 1024);
        ResourceResponseEnum.FILE_SIZE_EXCEEDED.message("文件大小超限：" + fileSize + " bytes，最大允许 " + maxMb + " MB（场景：" + scene.getCode() + "）")
                .assertTrue(overSizeCheck);

    }

    private ResourceStorageDriver resolveDriver(ResourceSceneConfig scene) {
        return switch (scene.getType()) {
            case LOCAL -> localDriver;
            case OSS -> {
                if (ossDriver == null) {
                    throw new IllegalStateException("场景 " + scene.getCode() + " 配置了 OSS 存储，但 OssClient 未在容器中注册，请引入 sz-common-oss 依赖");
                }
                yield ossDriver;
            }
        };
    }

    private String buildObjectKey(MultipartFile file, String namingKey, ResourceSceneConfig scene, String[] pathSegments) {
        String originalFilename = sanitizeOriginalFilename(file);
        String ext = extractExtension(originalFilename);

        // 1. 确定文件名（由 naming 规则决定）
        String filename = switch (scene.getNaming()) {
            case BIZ_KEY -> {
                ResourceResponseEnum.NAMING_KEY_REQUIRED.assertTrue(namingKey == null || namingKey.isBlank());
                yield sanitizeBizKey(namingKey) + "." + ext;
            }
            case UUID -> java.util.UUID.randomUUID().toString().replace("-", "") + "." + ext;
            case ORIGINAL -> sanitizeOriginalName(originalFilename);
            case TIMESTAMP -> LocalDateTime.now().format(TIMESTAMP_FORMATTER) + "." + ext;
        };

        // 2. 构建子目录段
        String subPath = buildSubPath(scene.getPathStrategy(), pathSegments);

        // 3. 拼接
        return joinPath(resolveSceneBasePath(scene), subPath, filename);
    }

    private String buildSubPath(PathStrategyEnum strategy, String[] pathSegments) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        return switch (strategy) {
            case FLAT -> "";
            case DATE -> today;
            case BIZ -> joinSegments(pathSegments);
            case BIZ_DATE -> joinPath(joinSegments(pathSegments), today);
        };
    }

    private String resolveSceneBasePath(ResourceSceneConfig scene) {
        String base = (scene.getType() == StorageTypeEnum.OSS) ? scene.getBucket() : scene.getPath();
        return normalizeBase(base);
    }

    /**
     * 规范化 basePath：去除 "./" 前缀、去除首尾 "/"，null/blank 统一返回空串
     */
    private String normalizeBase(String base) {
        if (base == null || base.isBlank()) {
            return "";
        }
        while (base.startsWith("./")) {
            base = base.substring(2);
        }
        if (base.startsWith("/")) {
            base = base.substring(1);
        }
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    private String buildPublicUrl(String objectKey, ResourceSceneConfig scene) {
        if (scene.getBaseUrl() == null || scene.getBaseUrl().isBlank()) {
            return null;
        }
        if (objectKey == null || objectKey.isBlank()) {
            return scene.getBaseUrl();
        }
        String trimmedBase = scene.getBaseUrl().endsWith("/") ? scene.getBaseUrl().substring(0, scene.getBaseUrl().length() - 1) : scene.getBaseUrl();

        // 截掉 objectKey 中的 path/prefix 前缀，剩余部分拼在 base-url 后
        // objectKey 完整存库（含 path/prefix），base-url 是用户任意填写的访问根地址
        String relPath = stripScenePrefix(objectKey, resolveSceneBasePath(scene));
        if (relPath.isBlank()) {
            return trimmedBase;
        }
        return trimmedBase + "/" + relPath;
    }

    /**
     * 生成 S3 Presigned URL（仅 OSS 场景）
     * <p>
     * 委托给 {@link ResourceStorageDriver#presign}，由具体驱动负责签名生成。 签名有效期取自场景配置
     * {@code scene.expire}，由启动校验保证必然 &gt; 0。
     * </p>
     */
    private String buildPresignedUrl(String objectKey, ResourceSceneConfig scene) {
        ResourceStorageDriver driver = resolveDriver(scene);
        return driver.presign(objectKey, scene, scene.getExpire());
    }

    /**
     * 截掉 objectKey 中的场景前缀（path 或 prefix）
     * <p>
     * 若 objectKey 以场景前缀开头，截掉后返回相对部分； 否则容错返回 objectKey 原值，不抛异常。
     * </p>
     *
     * @param objectKey
     *            完整 objectKey，如 "avatars/20260416/xxx.jpg"
     * @param basePath
     *            场景前缀，如 "avatars"（来自 path 或 prefix）
     * @return 截掉前缀后的相对路径，如 "20260416/xxx.jpg"
     */
    private String stripScenePrefix(String objectKey, String basePath) {
        if (basePath == null || basePath.isBlank()) {
            return objectKey;
        }
        // 统一末尾加 "/"，确保精确匹配前缀边界（避免 "avatar" 误匹配 "avatars/..."）
        String prefix = basePath.endsWith("/") ? basePath : basePath + "/";
        if (objectKey.startsWith(prefix)) {
            return objectKey.substring(prefix.length());
        }
        // 容错：objectKey 不以 path/prefix 开头时，直接返回原值
        return objectKey;
    }

    private String joinPath(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append('/');
            }
            sb.append(part);
        }
        return sb.toString();
    }

    private String joinSegments(String[] segments) {
        if (segments == null || segments.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String seg : segments) {
            if (seg == null || seg.isBlank()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append('/');
            }
            sb.append(sanitizePathSegment(seg));
        }
        return sb.toString();
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private String sanitizeBizKey(String namingKey) {
        String sanitized = namingKey.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        ResourceResponseEnum.NAMING_KEY_REQUIRED.message("namingKey 不合法：" + namingKey).assertTrue(sanitized.isEmpty());
        return sanitized;
    }

    /**
     * 清洗原始文件名用作最终文件名（ORIGINAL 命名规则）： 只保留字母、数字、下划线、连字符、点
     */
    private String sanitizeOriginalName(String originalFilename) {
        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9_\\-.\\u4e00-\\u9fa5]", "_");
        if (sanitized.isEmpty() || sanitized.equals(".")) {
            return "file." + extractExtension(originalFilename);
        }
        return sanitized;
    }

    private String sanitizePathSegment(String segment) {
        String sanitized = segment.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        ResourceResponseEnum.INVALID_PATH_SEGMENT.message("路径分段不合法：" + segment).assertTrue(sanitized.isEmpty());
        return sanitized;
    }

    /**
     * ORIGINAL 命名规则冲突处理：在文件名（不含扩展名）末尾追加 "_毫秒时间戳"。 例：avatars/20260427/logo.png →
     * avatars/20260427/logo_1745742241123.png
     */
    private String appendTimestamp(String objectKey) {
        int dot = objectKey.lastIndexOf('.');
        if (dot <= 0) {
            return objectKey + "_" + System.currentTimeMillis();
        }
        return objectKey.substring(0, dot) + "_" + System.currentTimeMillis() + objectKey.substring(dot);
    }
}
