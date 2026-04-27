package com.sz.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OSS 对象存储配置属性
 *
 * <p>
 * 支持所有兼容 AWS S3 协议的存储服务，包括：
 * <ul>
 * <li>MinIO（自建）</li>
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
     * 服务提供商类型。 影响：URL 拼接风格（虚拟主机 vs 路径风格的默认值）。 可选值：minio（默认）、aliyun、tencent、qiniu
     */
    private OssProviderEnum provider = OssProviderEnum.MINIO;

    /**
     * S3 兼容服务的访问端点（endpoint）。
     * <p>
     * - MinIO：http://127.0.0.1:9000 或 http://127.0.0.1:9000/（末尾斜杠均可） -
     * 阿里云：s3.oss-cn-hangzhou.aliyuncs.com（不含协议前缀） -
     * 腾讯云：cos.ap-guangzhou.myqcloud.com（不含协议前缀） -
     * 七牛云：s3.cn-east-1.qiniucs.com（不含协议前缀）
     * <p>
     * 注意：含协议前缀（http:// / https://）时，scheme 字段的设置会被忽略。
     */
    private String endpoint;

    /**
     * 访问密钥 ID（Access Key ID / SecretID）。
     */
    private String accessKey;

    /**
     * 访问密钥密码（Access Key Secret / SecretKey）。
     */
    private String secretKey;

    /**
     * 默认存储空间名称（Bucket Name）。 上传时未指定 bucket 则使用此值。
     */
    private String bucketName;

    /**
     * 自定义访问域名（CDN 域名或自定义源站域名）。
     * <p>
     * - 若含协议前缀（http:// / https://），直接使用； - 若不含协议前缀，会自动拼接 scheme。
     * <p>
     * 配置后，文件访问 URL 将以此域名为基础，而非 endpoint。 MinIO 场景下 domain 应包含 bucket（如
     * http://host:9000/bucket）， 或不配置 domain 由代码自动拼接。
     */
    private String domain;

    /**
     * 访问协议（http 或 https）。
     * <p>
     * 注意：此字段替代了已废弃的 is-https 配置项。 若 endpoint 中已包含协议前缀（http:// / https://），则此字段对
     * endpoint 连接无效， 仅影响 getUrl() 拼接的公开访问 URL。
     * <p>
     * 默认值：https
     */
    private SchemeEnum scheme = SchemeEnum.https;

    /**
     * AWS S3 签名所用 Region。
     * <p>
     * 影响范围： 1. S3 客户端初始化（签名算法需要 region） 2. Presigned URL 中的 X-Amz-Credential 字段（含
     * region）
     * <p>
     * 各厂商说明： - MinIO：填任意值均可（MinIO 不校验），默认 us-east-1 即可 - 阿里云 OSS：填任意值均可（OSS 不校验
     * region），建议填 us-east-1 或 cn-hangzhou - 腾讯云 COS：【必须填写正确值】，如
     * ap-guangzhou、ap-beijing 等，否则 Presigned URL 403 - 七牛云 Kodo：【必须填写正确值】，如
     * cn-east-1、cn-south-1 等，否则 Presigned URL 403
     * <p>
     * 默认值：us-east-1（适用于 MinIO 和阿里云）
     */
    private String region = "us-east-1";

    /**
     * 强制使用路径风格访问（Path-Style）。
     * <p>
     * - true：路径风格，如 http://endpoint/bucket/object（适合 MinIO、Ceph 等自建服务） -
     * false：虚拟主机风格，如 http://bucket.endpoint/object（适合阿里云、腾讯云、七牛云） -
     * null（默认）：自动判断，provider = MINIO 时为 true，其余为 false
     * <p>
     * 七牛云同时支持两种风格，设 false 或 null 均可。
     */
    private Boolean forcePathStyle;

    /**
     * 判断是否使用路径风格访问。 若 forcePathStyle 已显式配置，直接返回配置值； 否则自动判断：MinIO
     * 使用路径风格，其他厂商使用虚拟主机风格。
     *
     * @return true 表示路径风格，false 表示虚拟主机风格
     */
    public boolean resolvePathStyle() {
        if (forcePathStyle != null) {
            return forcePathStyle;
        }
        return OssProviderEnum.MINIO.equals(provider);
    }

}
