package com.sz.resource.config;

import com.sz.oss.OssProperties;
import com.sz.oss.OssSceneConnection;
import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import com.sz.resource.util.PathSanitizer;
import lombok.Data;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
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
 * YAML 场景配置通过 sz.resource 绑定，修改配置需重启服务。启动时校验静态场景； 自定义场景 provider
 * 的配置在解析时应用相同的校验和默认值规则。
 * </p>
 *
 * <pre>
 * sz:
 *   resource:
 *     root: ./data
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
 *         max-size: 2MB
 *       - code: user.avatar
 *         type: LOCAL
 *         serve-mode: DIRECT
 *         path: avatars/
 *         base-url: http://127.0.0.1:5000/api/admin/resource/file/avatars
 *         naming: UUID
 *         path-strategy: BIZ_DATE
 *         exts: [png, jpg, jpeg, webp, gif]
 *         max-size: 5MB
 * </pre>
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "sz.resource")
public class ResourceProperties {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient OssProperties ossProperties;

    @Autowired(required = false)
    void setOssDefaults(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    /**
     * LOCAL 存储使用的全局物理根目录。LOCAL 场景生成的 objectKey 已包含场景 path 和分层子目录，实际文件路径为
     * root/objectKey；OSS 场景不使用本字段。相对路径基于应用进程工作目录；YAML null 或精确空串会绑定为空串并解析为工作
     * 目录，应填写明确目录。
     *
     * @schemaExample ./data | 将本地资源存放在应用进程工作目录下的 data 目录
     */
    private String root = "./data";

    /**
     * 保留的全局默认存储类型。当前实现不读取它作为场景 type 的回退值；场景未配置 type 时始终使用 ResourceSceneConfig.type
     * 自身的初始化默认值。
     */
    private StorageTypeEnum defaultStorageType = StorageTypeEnum.LOCAL;

    /**
     * 本地开发辅助文件服务。未配置时关闭，是否注册仅由 enabled 配置决定，不额外限制 profile。 仓库 local
     * 配置显式开启，dev、preview、prod 配置默认关闭；任何环境显式设为 true 均会注册。 只控制配套 admin 开发文件
     * Controller 的注册，不影响上传、LOCAL 存储或 DIRECT URL 生成。 部署环境使用 Nginx 提供 LOCAL DIRECT
     * 静态访问，并配置对应场景的实际 base-url。
     */
    private DevFileServerConfig devFileServer = new DevFileServerConfig();

    /**
     * 全局资源上传安全策略。省略本段，或配置 YAML null、精确空串时，保留初始化的 SecurityConfig 对象；只含空白字符的非空
     * 字符串会绑定失败。运行时该对象必须非 null，否则默认 YmlSecurityPolicyProvider 无法读取策略。容器未提供自定义
     * ResourceSecurityPolicyProvider 时，默认 provider 读取本段：未配置字段分别使用内置扩展名白名单、MIME 白名单和
     * 50MB 大小上限；硬编码危险扩展名黑名单始终优先且不可覆盖。上层若注册数据库、配置中心等自定义
     * ResourceSecurityPolicyProvider，可替代本段提供的全局策略。
     */
    private SecurityConfig security = new SecurityConfig();

    /**
     * 静态资源场景列表。每项按 code 建立索引，code 必须非空白且唯一；列表未配置或为空时资源模块记录警告并不可用。每个场景 会在启动时应用默认值并执行
     * LOCAL/OSS、连接、访问模式等组合校验；自定义 ResourceSceneProvider 提供的动态场景也执行 同一解析规则。
     */
    private List<ResourceSceneConfig> scenes = new ArrayList<>();

    /**
     * 场景配置的 Map 缓存（code → config），在 {@link #validate()} 中构建
     */
    @Setter(lombok.AccessLevel.NONE)
    private transient Map<String, ResourceSceneConfig> sceneMap = Collections.emptyMap();

    /**
     * 本地开发辅助文件服务配置。
     */
    @Data
    public static class DevFileServerConfig {

        /**
         * 是否注册本地开发文件读取入口，默认 false，不额外限制 profile。false 或未配置时不创建 Controller，也不注册路由。
         * 建议只在本地临时验证时开启；部署环境默认关闭并使用 Nginx，显式设为 true 时也会启用。 入口匿名读取唯一匹配的 LOCAL DIRECT
         * 场景，不读取 PROTECTED 或 OSS 场景；上传等接口不受本开关影响。
         */
        private boolean enabled = false;
    }

    /**
     * 全局安全策略
     */
    @Data
    public static class SecurityConfig {

        /**
         * 全局允许上传的文件扩展名白名单，值应使用小写且不带点。未配置或空集合时使用内置的常见图片、文档、压缩包和音视频
         * 扩展名集合。上传时先执行不可覆盖的危险扩展名黑名单和双扩展名检查，再检查本白名单；即使把黑名单扩展名写入此处也 不会放行。
         */
        private Set<String> allowedExts;

        /**
         * 全局允许上传的 MIME 类型白名单，值应使用小写标准 MIME。未配置或空集合时使用内置默认集合。客户端 MIME 非空白且 场景未配置非空 mimes
         * 时，将客户端传入的完整值转为小写后与本集合精确匹配，不解析或去除 charset 等参数；场景 mimes
         * 非空时完全使用场景集合，不再检查本集合。客户端未提供 MIME 或值为空白时跳过 MIME 校验。
         */
        private Set<String> allowedMimeTypes;

        /**
         * 默认 YmlSecurityPolicyProvider 使用的全局单文件大小上限，绑定为 Spring
         * DataSize。支持裸字节整数或整数串，以及精确 大写 B、KB、MB、GB、TB；字符串内 Java Character.isWhitespace
         * 空白会移除，KB 及以上按 1024 进制换算。未配置时 使用内置 50MB；配置小于等于 0
         * 不会关闭限制，上传仍把该值作为全局上限并与场景上限比较，会拒绝非空文件。自定义 ResourceSecurityPolicyProvider 可替代本值。
         *
         * @schemaExample 50MB | 默认全局单文件大小上限
         */
        private DataSize maxSize;

        /**
         * 获取生效的扩展名白名单（非空配置值优先，未配或为空集合则返回默认值）
         */
        public Set<String> getEffectiveAllowedExts() {
            return (allowedExts != null && !allowedExts.isEmpty()) ? allowedExts : ResourceSecurityDefaults.DEFAULT_ALLOWED_EXTS;
        }

        /**
         * 获取生效的 MIME 白名单（非空配置值优先，未配或为空集合则返回默认值）
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
     * @return 不可修改映射结构的 Map 视图，场景配置对象仍引用原配置
     */
    public Map<String, ResourceSceneConfig> getSceneMap() {
        return sceneMap;
    }

    /**
     * 基于全局配置解析单个场景的生效配置。
     *
     * <p>
     * 返回独立副本，供自定义 {@code ResourceSceneProvider} 的运行时场景使用，避免补默认值时修改 provider
     * 持有的原始对象。
     */
    public ResourceSceneConfig resolveEffectiveScene(ResourceSceneConfig scene) {
        if (scene == null) {
            return null;
        }
        ResourceSceneConfig effective = copyScene(scene);
        validateAndApplyScene(effective);
        return effective;
    }

    /**
     * 启动时校验所有场景配置的必填字段，配置有误直接抛异常终止启动
     *
     * <p>
     * 校验规则：
     * <ul>
     * <li>type=LOCAL 时 path 不能为空，且不能包含路径穿越字符</li>
     * <li>type=OSS 时 bucket 可以继承 sz.oss.bucketName，且不能包含路径穿越字符</li>
     * <li>type=LOCAL 时不接受 OSS 专用连接字段、bucket 或寻址风格</li>
     * <li>scene 覆写 endpoint 或任一凭据时，endpoint、accessKey、secretKey 必须成组完整配置</li>
     * <li>serveMode=DIRECT + type=OSS 时，缺省 baseUrl 按生效连接、bucket 和寻址风格推导</li>
     * <li>serveMode=DIRECT + type=LOCAL 时 baseUrl 不能为空</li>
     * <li>serveMode=PRESIGNED + type=LOCAL 组合不允许</li>
     * <li>serveMode=PRESIGNED 时 expire 必须 &gt; 0</li>
     * </ul>
     */
    @PostConstruct
    public void validate() {
        if (scenes == null || scenes.isEmpty()) {
            log.warn("[Resource] 未配置任何资源场景（sz.resource.scenes），资源模块将不可用");
            return;
        }
        Map<String, ResourceSceneConfig> map = new LinkedHashMap<>();
        for (ResourceSceneConfig scene : scenes) {
            String code = scene.getCode();
            if (code == null || code.isBlank()) {
                throw new IllegalStateException("[Resource] 场景配置缺少 code 字段，请检查 sz.resource.scenes 配置");
            }
            if (map.containsKey(code)) {
                throw new IllegalStateException("[Resource] 场景编码重复：" + code + "，请检查 sz.resource.scenes 配置");
            }

            validateAndApplyScene(scene);
            map.put(code, scene);
        }
        this.sceneMap = Collections.unmodifiableMap(map);
        log.info("[Resource] 场景配置自检通过，共 {} 个场景", map.size());
    }

    private void validateAndApplyScene(ResourceSceneConfig scene) {
        String code = scene.getCode();
        if (code == null || code.isBlank()) {
            throw new IllegalStateException("[Resource] 场景配置缺少 code 字段，请检查 sz.resource.scenes 配置");
        }
        if (scene.getType() == StorageTypeEnum.LOCAL) {
            if (hasOssOnlyConfiguration(scene)) {
                throw new IllegalStateException("[Resource] 场景 [" + code + "] type=LOCAL 不能配置 OSS 专用字段");
            }
            if (scene.getPath() == null || scene.getPath().isBlank()) {
                throw new IllegalStateException("[Resource] 场景 [" + code + "] type=LOCAL 但未配置 path，请检查 sz.resource.scenes 配置");
            }
            try {
                PathSanitizer.validate(scene.getPath(), PathSanitizer.Mode.CONFIG);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("[Resource] 场景 [" + code + "] path 配置非法：" + e.getMessage());
            }
        }
        if (scene.getType() == StorageTypeEnum.OSS) {
            OssSceneConnection connection = scene.toOssSceneConnection();
            if (connection != null) {
                connection.validateRequired(code);
            }
            if (scene.getBucket() == null || scene.getBucket().isBlank()) {
                if (ossProperties == null) {
                    throw new IllegalStateException("[Resource] 场景 [" + code + "] type=OSS 未配置 bucket，且 sz.oss.bucketName 为空");
                }
                try {
                    scene.setBucket(ossProperties.resolveBucketName(null));
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("[Resource] 场景 [" + code + "] type=OSS 未配置 bucket，且 sz.oss.bucketName 为空", e);
                }
            }
            try {
                PathSanitizer.validate(scene.getBucket(), PathSanitizer.Mode.CONFIG);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("[Resource] 场景 [" + code + "] bucket 配置非法：" + e.getMessage());
            }
        }
        // scene 显式 base-url（完整 HTTP(S) 地址或 / 开头同源路径）同级且优先，不根据 endpoint 补全。
        // 仅缺省时推导 OSS 地址：默认连接 domain 优先于 endpoint；独立连接只使用自身 endpoint。
        // LOCAL DIRECT 不继承 OSS 直连地址，必须显式配置；其他访问模式不使用 base-url。
        if (scene.getServeMode() == ServeModeEnum.DIRECT && (scene.getBaseUrl() == null || scene.getBaseUrl().isBlank())) {
            if (scene.getType() == StorageTypeEnum.OSS && ossProperties != null) {
                OssSceneConnection connection = scene.toOssSceneConnection();
                scene.setBaseUrl(connection != null
                        ? connection.resolvePublicUrl(scene.getBucket(), scene.getAddressingStyle(), ossProperties)
                        : ossProperties.resolvePublicUrl(scene.getBucket(), scene.getAddressingStyle()));
            }
            if (scene.getBaseUrl() == null || scene.getBaseUrl().isBlank()) {
                throw new IllegalStateException("[Resource] 场景 [" + code + "] serveMode=DIRECT 但未配置 base-url，且无可用的 OSS 公共地址");
            }
        }
        if (scene.getServeMode() == ServeModeEnum.PRESIGNED && scene.getType() == StorageTypeEnum.LOCAL) {
            throw new IllegalStateException("[Resource] 场景 [" + code + "] serveMode=PRESIGNED 不能与 type=LOCAL 组合使用，PRESIGNED 仅支持 OSS 存储");
        }
        if (scene.getServeMode() == ServeModeEnum.PRESIGNED && (scene.getExpire() == null || scene.getExpire() <= 0)) {
            throw new IllegalStateException("[Resource] 场景 [" + code + "] serveMode=PRESIGNED 但 expire 无效，必须 > 0（单位：秒）");
        }
    }

    private ResourceSceneConfig copyScene(ResourceSceneConfig source) {
        ResourceSceneConfig copy = new ResourceSceneConfig();
        copy.setCode(source.getCode());
        copy.setName(source.getName());
        copy.setType(source.getType());
        copy.setServeMode(source.getServeMode());
        copy.setExpire(source.getExpire());
        copy.setPath(source.getPath());
        copy.setBaseUrl(source.getBaseUrl());
        copy.setBucket(source.getBucket());
        copy.setAddressingStyle(source.getAddressingStyle());
        copy.setEndpoint(source.getEndpoint());
        copy.setAccessKey(source.getAccessKey());
        copy.setSecretKey(source.getSecretKey());
        copy.setProvider(source.getProvider());
        copy.setRegion(source.getRegion());
        copy.setNaming(source.getNaming());
        copy.setPathStrategy(source.getPathStrategy());
        copy.setExts(source.getExts());
        copy.setMimes(source.getMimes());
        copy.setMaxSize(source.getMaxSize());
        return copy;
    }

    private boolean hasOssOnlyConfiguration(ResourceSceneConfig scene) {
        return scene.getAddressingStyle() != null || scene.getEndpoint() != null || scene.getAccessKey() != null || scene.getSecretKey() != null
                || scene.getProvider() != null || scene.getRegion() != null || scene.getBucket() != null;
    }
}
