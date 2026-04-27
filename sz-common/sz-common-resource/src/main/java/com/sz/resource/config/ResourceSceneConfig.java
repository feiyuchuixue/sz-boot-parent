package com.sz.resource.config;

import com.sz.resource.enums.NamingRuleEnum;
import com.sz.resource.enums.PathStrategyEnum;
import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import lombok.Data;

import java.util.Set;

/**
 * 单个资源场景配置
 *
 * <pre>
 * sso:
 *   resource:
 *     root: ./data
 *     scenes:
 *       - code: sso.provider.logo
 *         type: LOCAL
 *         serve-mode: DIRECT
 *         path: providers/
 *         base-url: http://127.0.0.1:5000/api/admin/resource/file/providers
 *         naming: BIZ_KEY
 *         exts: [svg, png, jpg, jpeg, webp]
 *         max-size: 2
 *
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
@Data
public class ResourceSceneConfig {

    /**
     * 场景唯一编码，如 "sso.provider.logo"
     */
    private String code;

    /**
     * 存储类型：LOCAL（本地磁盘）/ OSS（委托 sz-common-oss）
     */
    private StorageTypeEnum type = StorageTypeEnum.LOCAL;

    /**
     * 访问模式：DIRECT（明文直接访问）/ TOKEN（一次性加密token+代理）/ PRESIGNED（S3 Presigned URL）
     */
    private ServeModeEnum serveMode = ServeModeEnum.DIRECT;

    /**
     * 临时 URL 有效期（秒），serveMode=PRESIGNED 或 TOKEN 时有效，默认 3600 秒
     */
    private Long expire = 3600L;

    /**
     * 本地存储基础目录（type=LOCAL 时有效），相对于全局 root
     * <p>
     * 示例：path=avatars/，root=./data → 物理根目录为 ./data/avatars/
     * </p>
     */
    private String path;

    /**
     * PUBLIC 资源的访问基础 URL（不含末尾斜杠）
     * 示例：http://127.0.0.1:5000/api/admin/resource/public/avatars
     */
    private String baseUrl;

    /**
     * OSS 存储桶名称（type=OSS 时必填）
     * <p>
     * 同时承担两个职责：
     * <ol>
     * <li>作为 OSS SDK 调用时的 bucket 参数（命名空间）</li>
     * <li>作为 objectKey 的一级前缀，便于 base-url 还原与 URL 拼接</li>
     * </ol>
     * 示例：bucket=client-logos → objectKey=client-logos/20260418/xxx.png； 实际写入 OSS
     * 时由驱动剥离 bucket 段，物理 key 为 20260418/xxx.png。
     * </p>
     */
    private String bucket;

    /**
     * 文件命名规则：BIZ_KEY（业务 key 命名，可覆盖）/ UUID（随机命名，不覆盖）
     */
    private NamingRuleEnum naming = NamingRuleEnum.UUID;

    /**
     * 路径分层策略（type=LOCAL 时有效），默认 FLAT（平铺）
     * <ul>
     * <li>FLAT - 平铺，无子目录：path/filename</li>
     * <li>DATE - 按日期自动分片：path/yyyyMMdd/filename</li>
     * <li>BIZ - 按业务层级分层：path/{pathSegments}/filename</li>
     * <li>BIZ_DATE - 先业务层级后日期：path/{pathSegments}/yyyyMMdd/filename</li>
     * </ul>
     */
    private PathStrategyEnum pathStrategy = PathStrategyEnum.FLAT;

    /**
     * 允许上传的文件扩展名（小写，无点），为空则不限制 示例：["svg", "png", "jpg"]
     */
    private Set<String> exts;

    /**
     * 允许上传的 MIME 类型，为空则不限制
     */
    private Set<String> mimes;

    /**
     * 最大允许文件大小（MB），0 表示不限制
     */
    private long maxSize = 0;
}
