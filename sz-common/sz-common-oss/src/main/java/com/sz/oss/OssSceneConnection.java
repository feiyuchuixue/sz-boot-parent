package com.sz.oss;

import lombok.Data;

import java.net.URI;

/**
 * 资源场景使用的 S3 连接覆盖配置。
 * <p>
 * endpoint、accessKey、secretKey 中任一项出现即视为独立连接，并要求三项完整；否则连接端点和凭据继承全局配置。 provider
 * 和 region 可在继承全局连接时单独覆盖寻址风格默认值和签名区域。
 * </p>
 */
@Data
public class OssSceneConnection {

    private OssProviderEnum provider;

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String region;

    public URI resolveEndpointUri(OssProperties global) {
        return OssProperties.resolveEndpointUri(hasIndependentConnection() ? endpoint : global.getEndpoint(), global.getScheme());
    }

    public String resolvePublicUrl(String bucket, OssAddressingStyle style, OssProperties global) {
        String resolvedBucket = global.resolveBucketName(bucket);
        if (!hasIndependentConnection()) {
            return global.resolvePublicUrl(resolvedBucket, style, provider);
        }
        return OssProperties.resolvePublicUrl(endpoint, null, resolvedBucket, global.getScheme(), global.resolvePathStyle(style, provider));
    }

    public OssProviderEnum resolveProvider(OssProperties global) {
        return provider == null ? global.getProvider() : provider;
    }

    public String resolveRegion(OssProperties global) {
        String resolved = region == null || region.isBlank() ? global.getRegion() : region.trim();
        return resolved == null || resolved.isBlank() ? "us-east-1" : resolved.trim();
    }

    public String resolveAccessKey(OssProperties global) {
        return hasIndependentConnection() ? accessKey : global.getAccessKey();
    }

    public String resolveSecretKey(OssProperties global) {
        return hasIndependentConnection() ? secretKey : global.getSecretKey();
    }

    public boolean hasIndependentConnection() {
        return endpoint != null || accessKey != null || secretKey != null;
    }

    public void validateRequired(String sceneCode) {
        if (!hasIndependentConnection()) {
            return;
        }
        requireText(sceneCode, "endpoint", endpoint);
        requireText(sceneCode, "access-key", accessKey);
        requireText(sceneCode, "secret-key", secretKey);
    }

    private void requireText(String sceneCode, String field, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("OSS 场景 [" + sceneCode + "] 的独立连接缺少 " + field + " 配置");
        }
    }

}
