package com.sz.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

/**
 * OSS 对象存储配置属性
 *
 * <p>
 * 支持所有兼容 AWS S3 协议的存储服务，包括：
 * <ul>
 * <li>MinIO（自建）</li>
 * <li>RustFS（自建）</li>
 * <li>阿里云 OSS</li>
 * <li>腾讯云 COS</li>
 * <li>七牛云 Kodo</li>
 * <li>其他兼容 S3 协议的存储（Ceph、Garage 等）</li>
 * </ul>
 *
 * <p>
 * 各厂商关键配置差异请参考项目文档：docs/oss-provider-guide.md
 *
 * @author sz
 * @since 2024/11/12 14:39
 */
@Data
@Component
@ConfigurationProperties(prefix = "sz.oss")
public class OssProperties {

    /**
     * 全局对象存储服务提供商。provider 不切换 endpoint；未显式指定寻址风格时，由生效 provider 判定寻址风格。
     */
    private OssProviderEnum provider = OssProviderEnum.MINIO;

    /**
     * 全局 S3 API 连接端点。本属性控制 S3 客户端配置是否装配，缺省时不装配；仅配置场景 endpoint 不会独立启用 OSS。装配后 会先使用全局
     * endpoint、region、access-key、secret-key 创建默认客户端和预签名器，因此即使场景另有独立连接，全局连接
     * 参数仍须可用。支持完整 HTTP(S) 地址，也支持不带协议的主机名或主机名加端口；解析时去除首尾空白和末尾斜杠，未带协议 时由 scheme
     * 补全，已带协议时沿用端点自身协议。默认连接的 domain 未配置或为空白，且 OSS DIRECT 场景未显式配置 base-url
     * 时，也用本值推导直连地址；空白端点在创建客户端或推导地址时会报错。
     *
     * @schemaExample ${OSS_ENDPOINT:http://127.0.0.1:9000} | 使用环境变量配置 S3 API 端点
     */
    private String endpoint;

    /**
     * 全局默认 S3 连接的访问密钥 ID（Access Key ID 或 SecretID），供 SDK 和预签名认证使用。S3 客户端配置启用后会先用本值
     * 装配默认客户端，因此本值必须可用于全局连接；场景未形成独立连接时也继承此值。字段没有业务默认值。
     *
     * @schemaExample ${OSS_ACCESS_KEY} | 从环境变量读取访问密钥 ID
     */
    private String accessKey;

    /**
     * 全局默认 S3 连接的访问密钥密码（Access Key Secret 或 SecretKey），供 SDK 和预签名认证使用。S3
     * 客户端配置启用后会先用 本值装配默认客户端，因此本值必须可用于全局连接；场景未形成独立连接时也继承此值。字段没有业务默认值。
     *
     * @schemaExample ${OSS_SECRET_KEY} | 从环境变量读取访问密钥密码
     */
    private String secretKey;

    /**
     * 全局默认存储桶名称。OSS 场景的 bucket 未配置或为空白时继承此值；场景值和全局值都为空白会使场景校验失败。生效 bucket 既作为 SDK 的
     * bucket 参数，也作为资源 objectKey 的一级逻辑前缀。
     *
     * @schemaExample ${OSS_BUCKET:sz-admin} | 使用环境变量配置默认存储桶
     */
    private String bucketName;

    /**
     * 全局默认连接的 CDN 域名或自定义源站根地址，供 OssClient 生成直连 URL 前缀，也用于未显式配置 scene base-url 的 OSS
     * DIRECT 地址推导；不参与 SDK 请求或预签名。带 HTTP(S) 协议时直接使用，不带协议时由 scheme 补全。 PATH 风格下应填写不含
     * bucket 的访问根，代码会追加生效 bucket；VIRTUAL_HOSTED 风格下视为已指向桶的完整地址，或用 {bucket}
     * 占位符生成桶地址，PATH 风格使用 {bucket} 会报错。未配置或为空白时回退 endpoint；场景独立连接只用自身 endpoint，不使用此
     * domain。
     *
     * 本字段只决定返回的地址字符串，不开放存储权限或应用路由。
     *
     * @schemaExample ${OSS_DOMAIN:https://files.example.com} | 使用环境变量配置直连地址根
     */
    private String domain;

    /**
     * 全局地址补全协议。endpoint 自带 http:// 或 https:// 时不覆盖 endpoint 协议；endpoint、domain
     * 或场景独立 endpoint 未带协议时使用本值补全，domain 未配置或为空白时的直连地址沿用生效 endpoint 协议。
     */
    private SchemeEnum scheme = SchemeEnum.https;

    /**
     * 全局 AWS S3 签名 Region，影响 S3 客户端签名和 Presigned URL，不会切换 endpoint。场景 region
     * 未配置或为空白时继承 本值，本值仍为空白时解析器最终回退 us-east-1。按地域校验签名的服务必须填写 bucket 所在实际区域。
     *
     * @schemaExample us-east-1 | S3 兼容服务常用签名区域
     */
    private String region = "us-east-1";

    /**
     * 兼容旧配置的全局 S3 寻址风格开关：true 表示 PATH，地址形如 endpoint/bucket/object；false 表示
     * VIRTUAL_HOSTED，地址形如 bucket.endpoint/object；未配置时再看 addressing-style 和
     * provider。有效优先级为场景 addressing-style、全局 addressing-style、本字段、生效 provider
     * 判定值。若本字段与全局 addressing-style 同时配置且 语义冲突，会在应用场景覆盖前报错，因此场景覆盖不能绕过该冲突。
     */
    private Boolean forcePathStyle;

    /**
     * 全局显式 S3 寻址风格。有效优先级为场景 addressing-style、本字段、旧 force-path-style、生效 provider
     * 判定值；未配置时 继续回退旧开关及 provider。若本字段与 force-path-style 同时配置且语义冲突，会在应用场景覆盖前报错。
     */
    private OssAddressingStyle addressingStyle;

    /** 识别旧命名实例配置并提示迁移，防止绑定时静默回退默认连接。 */
    @Deprecated
    public void setInstances(Map<String, ?> instances) {
        if (instances != null && !instances.isEmpty()) {
            throw new IllegalStateException("sz.oss.instances 已移除，请将独立连接直接配置到 scene 顶层");
        }
    }

    /**
     * 判断是否使用路径风格访问。优先级依次为方法参数、addressingStyle、forcePathStyle；均未配置时， MinIO 和 RustFS
     * 使用路径风格，其他厂商使用虚拟主机风格。
     *
     * @return true 表示路径风格，false 表示虚拟主机风格
     */
    public boolean resolvePathStyle() {
        return resolvePathStyle(null, null);
    }

    public boolean resolvePathStyle(OssAddressingStyle override) {
        return resolvePathStyle(override, null);
    }

    public boolean resolvePathStyle(OssAddressingStyle override, OssProviderEnum providerOverride) {
        validateAddressingStyleCompatibility();
        if (override != null) {
            return override.isPathStyle();
        }
        if (addressingStyle != null) {
            return addressingStyle.isPathStyle();
        }
        if (forcePathStyle != null) {
            return forcePathStyle;
        }
        OssProviderEnum effectiveProvider = providerOverride == null ? provider : providerOverride;
        return OssProviderEnum.MINIO.equals(effectiveProvider) || OssProviderEnum.RUSTFS.equals(effectiveProvider);
    }

    /**
     * 解析并规范化 S3 endpoint。
     *
     * @return 带协议且不含末尾斜杠的 endpoint URI
     */
    public URI resolveEndpointUri() {
        return resolveEndpointUri(endpoint, scheme);
    }

    static URI resolveEndpointUri(String endpoint, SchemeEnum scheme) {
        String normalizedEndpoint = normalizeAddress(endpoint);
        if (normalizedEndpoint.isEmpty()) {
            throw new IllegalStateException("sz.oss.endpoint 未配置，请设置 S3 endpoint（如 http://127.0.0.1:9000）");
        }
        String endpointUri = hasHttpScheme(normalizedEndpoint) ? normalizedEndpoint : resolveScheme(scheme) + "://" + normalizedEndpoint;
        return URI.create(endpointUri);
    }

    /**
     * 解析直连 URL 前缀。
     * <p>
     * 路径风格会在直连地址根后拼接 bucket；虚拟主机风格在配置 domain 时直接使用 domain， 未配置 domain 时将 bucket 作为
     * endpoint 的子域名。
     *
     * @param bucket
     *            指定 bucket，空值时回退到全局 bucketName
     * @return 不含末尾斜杠的直连 URL 前缀
     */
    public String resolvePublicUrl(String bucket) {
        return resolvePublicUrl(bucket, null);
    }

    public String resolvePublicUrl(String bucket, OssAddressingStyle override) {
        return resolvePublicUrl(bucket, override, null);
    }

    public String resolvePublicUrl(String bucket, OssAddressingStyle override, OssProviderEnum providerOverride) {
        String resolvedBucket = resolveBucketName(bucket);
        return resolvePublicUrl(endpoint, domain, resolvedBucket, scheme, resolvePathStyle(override, providerOverride));
    }

    static String resolvePublicUrl(String endpoint, String domain, String resolvedBucket, SchemeEnum scheme, boolean pathStyle) {
        String normalizedDomain = normalizeAddress(domain);
        if (!normalizedDomain.isEmpty()) {
            String publicRoot = hasHttpScheme(normalizedDomain) ? normalizedDomain : resolveScheme(scheme) + "://" + normalizedDomain;
            if (publicRoot.contains("{bucket}")) {
                if (pathStyle) {
                    throw new IllegalStateException("domain 中的 {bucket} 占位符仅适用于 VIRTUAL_HOSTED 寻址风格");
                }
                return publicRoot.replace("{bucket}", resolvedBucket);
            }
            return pathStyle ? publicRoot + "/" + resolvedBucket : publicRoot;
        }

        URI endpointUri = resolveEndpointUri(endpoint, scheme);
        String endpointUrl = endpointUri.toString();
        if (pathStyle) {
            return endpointUrl + "/" + resolvedBucket;
        }
        String schemePrefix = endpointUri.getScheme() + "://";
        return schemePrefix + resolvedBucket + "." + endpointUrl.substring(schemePrefix.length());
    }

    /**
     * 解析最终 bucket，指定值为空时回退到全局 bucketName。
     */
    public String resolveBucketName(String bucket) {
        return resolveBucketName(bucket, bucketName);
    }

    static String resolveBucketName(String bucket, String defaultBucket) {
        String resolvedBucket = bucket == null ? "" : bucket.trim();
        if (resolvedBucket.isEmpty()) {
            resolvedBucket = defaultBucket == null ? "" : defaultBucket.trim();
        }
        if (resolvedBucket.isEmpty()) {
            throw new IllegalStateException("OSS bucket 未配置，请设置 sz.oss.bucketName 或场景 bucket");
        }
        return resolvedBucket;
    }

    private void validateAddressingStyleCompatibility() {
        if (addressingStyle != null && forcePathStyle != null && addressingStyle.isPathStyle() != forcePathStyle) {
            throw new IllegalStateException("sz.oss.addressing-style 与旧配置 sz.oss.force-path-style 冲突");
        }
    }

    private static String resolveScheme(SchemeEnum scheme) {
        return (scheme == null ? SchemeEnum.https : scheme).toString();
    }

    private static boolean hasHttpScheme(String address) {
        return address.regionMatches(true, 0, "http://", 0, "http://".length()) || address.regionMatches(true, 0, "https://", 0, "https://".length());
    }

    private static String normalizeAddress(String address) {
        if (address == null) {
            return "";
        }
        String normalized = address.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

}
