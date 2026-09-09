package com.sz.resource.config;

import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import com.sz.resource.util.PathSanitizer;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 统一资源管理配置
 * <p>
 * 场景配置在 application.yml 中静态定义，修改配置需重启服务。 启动时会对所有场景配置进行合法性自检，配置有误将直接终止启动。
 * </p>
 *
 * <pre>
 * sz:
 *   resource:
 *     root: ./data
 *     default-storage-type: LOCAL
 *     security:
 *       allowed-exts: [jpg, jpeg, png, gif, pdf, doc, docx]
 *       allowed-mime-types: [image/jpeg, image/png, application/pdf]
 *       max-size: 50MB
 *     scenes:
 *       - code: sso.provider.logo
 *         type: LOCAL
 *         serve-mode: DIRECT
 *         path: providers/
 *         base-url: http://127.0.0.1:5000/api/admin/resource/file/providers
 *         naming: BIZ_KEY
 *         exts: [svg, png, jpg, jpeg, webp]
 *         max-size: 2
 *       - code: user.avatar
 *         type: LOCAL
 *         serve-mode: DIRECT
 *         path: avatars/
 *         base-url: http://127.0.0.1:5000/api/admin/resource/file/avatars
 *         naming: UUID
 *         path-strategy: BIZ_DATE
 *         exts: [png, jpg, jpeg, webp, gif]
 *         max-size: 5
 * </pre>
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "sz.resource")
public class ResourceProperties {

    /**
     * 全局本地存储根目录（LOCAL 存储时有效）
     * <p>
     * objectKey 为相对于此目录的路径，物理路径 = root + "/" + objectKey。 场景配置中的 path 为相对于 root
     * 的基础目录。
     * </p>
     */
    private String root = "./data";

    /**
     * 全局默认存储类型，场景未指定 type 时使用此值
     */
    private StorageTypeEnum defaultStorageType = StorageTypeEnum.LOCAL;

    /**
     * 全局安全策略配置
     */
    private SecurityConfig security = new SecurityConfig();

    /**
     * 所有场景配置（List 格式，每个元素必须包含 code 字段）
     * <p>
     * 示例：
     * 
     * <pre>
     * scenes:
     *   - code: sso.provider.logo
     *     type: LOCAL
     *     serve-mode: DIRECT
     *     path: providers/
     * </pre>
     * </p>
     */
    private List<ResourceSceneConfig> scenes = new ArrayList<>();

    /**
     * 场景配置的 Map 缓存（code → config），在 {@link #validate()} 中构建
     */
    @Setter(lombok.AccessLevel.NONE)
    private transient Map<String, ResourceSceneConfig> sceneMap = Collections.emptyMap();

    /**
     * 全局安全策略
     */
    @Data
    public static class SecurityConfig {

        /**
         * 全局允许的文件扩展名白名单（小写，无点），未配时使用
         * {@link ResourceSecurityDefaults#DEFAULT_ALLOWED_EXTS}
         */
        private Set<String> allowedExts;

        /**
         * 全局允许的 MIME 类型白名单，未配时使用 {@link ResourceSecurityDefaults#DEFAULT_ALLOWED_MIMES}
         */
        private Set<String> allowedMimeTypes;

        /**
         * 全局最大文件大小，支持 Spring DataSize 格式（如 50MB、10KB）， 未配时使用
         * {@link ResourceSecurityDefaults#DEFAULT_MAX_SIZE_BYTES}（50MB）
         */
        private DataSize maxSize;

        /**
         * 获取生效的扩展名白名单（配置值优先，未配则返回默认值）
         */
        public Set<String> getEffectiveAllowedExts() {
            return (allowedExts != null && !allowedExts.isEmpty()) ? allowedExts : ResourceSecurityDefaults.DEFAULT_ALLOWED_EXTS;
        }

        /**
         * 获取生效的 MIME 白名单（配置值优先，未配则返回默认值）
         */
        public Set<String> getEffectiveAllowedMimeTypes() {
            return (allowedMimeTypes != null && !allowedMimeTypes.isEmpty()) ? allowedMimeTypes : ResourceSecurityDefaults.DEFAULT_ALLOWED_MIMES;
        }

        /**
         * 获取生效的最大文件大小（字节）
         */
        public long getEffectiveMaxSizeBytes() {
            return (maxSize != null) ? maxSize.toBytes() : ResourceSecurityDefaults.DEFAULT_MAX_SIZE_BYTES;
        }
    }

    /**
     * 根据场景 code 获取配置，找不到则抛异常
     *
     * @param sceneCode
     *            场景编码
     * @return 场景配置
     * @throws IllegalArgumentException
     *             场景不存在
     */
    public ResourceSceneConfig getScene(String sceneCode) {
        return Optional.ofNullable(sceneMap.get(sceneCode)).orElseThrow(() -> new IllegalArgumentException("未找到资源场景配置，sceneCode=" + sceneCode));
    }

    /**
     * 获取所有场景配置的 Map 视图（code → config）
     * <p>
     * 供 {@link com.sz.resource.spi.YmlResourceSceneProvider} 等调用方使用， 保持与原 Map 结构的
     * API 兼容。
     * </p>
     *
     * @return 不可变 Map
     */
    public Map<String, ResourceSceneConfig> getSceneMap() {
        return sceneMap;
    }

    /**
     * 启动时校验所有场景配置的必填字段，配置有误直接抛异常终止启动
     *
     * <p>
     * 校验规则：
     * <ul>
     * <li>type=LOCAL 时 path 不能为空，且不能包含路径穿越字符</li>
     * <li>type=OSS 时 bucket 必填，且不能包含路径穿越字符</li>
     * <li>serveMode=DIRECT 时 baseUrl 不能为空</li>
     * <li>serveMode=PRESIGNED + type=LOCAL 组合不允许</li>
     * <li>serveMode=PRESIGNED 时 expire 必须 &gt; 0</li>
     * </ul>
     */
    @PostConstruct
    public void validate() {
        if (scenes == null || scenes.isEmpty()) {
            log.warn("[Resource] 未配置任何资源场景（sso.resource.scenes），资源模块将不可用");
            return;
        }
        Map<String, ResourceSceneConfig> map = new LinkedHashMap<>();
        for (ResourceSceneConfig scene : scenes) {
            String code = scene.getCode();
            if (code == null || code.isBlank()) {
                throw new IllegalStateException("[Resource] 场景配置缺少 code 字段，请检查 sso.resource.scenes 配置");
            }
            if (map.containsKey(code)) {
                throw new IllegalStateException("[Resource] 场景编码重复：" + code + "，请检查 sso.resource.scenes 配置");
            }

            if (scene.getType() == StorageTypeEnum.LOCAL) {
                if (scene.getPath() == null || scene.getPath().isBlank()) {
                    throw new IllegalStateException("[Resource] 场景 [" + code + "] type=LOCAL 但未配置 path，请检查 sso.resource.scenes 配置");
                }
                try {
                    PathSanitizer.validate(scene.getPath(), PathSanitizer.Mode.CONFIG);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("[Resource] 场景 [" + code + "] path 配置非法：" + e.getMessage());
                }
            }
            if (scene.getType() == StorageTypeEnum.OSS) {
                if (scene.getBucket() == null || scene.getBucket().isBlank()) {
                    throw new IllegalStateException("[Resource] 场景 [" + code + "] type=OSS 但未配置 bucket，请检查 sso.resource.scenes 配置");
                }
                try {
                    PathSanitizer.validate(scene.getBucket(), PathSanitizer.Mode.CONFIG);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("[Resource] 场景 [" + code + "] bucket 配置非法：" + e.getMessage());
                }
            }
            if (scene.getServeMode() == ServeModeEnum.DIRECT) {
                if (scene.getBaseUrl() == null || scene.getBaseUrl().isBlank()) {
                    throw new IllegalStateException("[Resource] 场景 [" + code + "] serveMode=DIRECT 但未配置 base-url，将导致 resolveUrl 始终返回 null");
                }
            }
            if (scene.getServeMode() == ServeModeEnum.PRESIGNED && scene.getType() == StorageTypeEnum.LOCAL) {
                throw new IllegalStateException("[Resource] 场景 [" + code + "] serveMode=PRESIGNED 不能与 type=LOCAL 组合使用，PRESIGNED 仅支持 OSS 存储");
            }
            if (scene.getServeMode() == ServeModeEnum.PRESIGNED) {
                if (scene.getExpire() == null || scene.getExpire() <= 0) {
                    throw new IllegalStateException("[Resource] 场景 [" + code + "] serveMode=PRESIGNED 但 expire 无效，必须 > 0（单位：秒）");
                }
            }
            map.put(code, scene);
        }
        this.sceneMap = Collections.unmodifiableMap(map);
        log.info("[Resource] 场景配置自检通过，共 {} 个场景", map.size());
    }
}
