package com.sz.oss;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.net.URI;

/**
 * S3 客户端配置
 * <p>
 * 统一初始化 S3AsyncClient、S3Client、S3Presigner、S3TransferManager。 region、pathStyle
 * 等参数均从 {@link OssProperties} 读取， 支持 MinIO、阿里云 OSS、腾讯云 COS、七牛云 Kodo 等 S3 兼容存储。
 * </p>
 *
 * @author sz
 * @since 2024/11/12 15:16
 */
@Configuration
@RequiredArgsConstructor
public class S3Configuration {

    private final OssProperties ossProperties;

    /**
     * 异步 S3 客户端（基于 AWS CRT）。
     * <p>
     * CRT 客户端没有独立的 chunkedEncodingEnabled() 入口， 但
     * {@code checksumValidationEnabled(false)} 可关闭本地 checksum 计算， 避免与阿里云 /
     * 腾讯云的签名冲突。 若遇阿里云 OSS 上传 403，请确保 oss.endpoint 使用 Bucket 所在地域 endpoint， 并将
     * oss.provider 设置为 aliyun。
     * </p>
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        StaticCredentialsProvider credentialsProvider = credentialsProvider();
        return S3AsyncClient.crtBuilder().region(getRegion()).forcePathStyle(ossProperties.resolvePathStyle()).credentialsProvider(credentialsProvider)
                .endpointOverride(getUri()).targetThroughputInGbps(20.0).checksumValidationEnabled(false).build();
    }

    @Bean
    public S3Client s3Client() {
        StaticCredentialsProvider credentialsProvider = credentialsProvider();
        return S3Client.builder().region(getRegion()).forcePathStyle(ossProperties.resolvePathStyle()).credentialsProvider(credentialsProvider)
                .endpointOverride(getUri()).build();
    }

    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder().s3Client(s3AsyncClient).build();
    }

    /**
     * Presigned URL 生成器。
     * <p>
     * {@code chunkedEncodingEnabled(false)} 对阿里云 OSS、腾讯云 COS 生成正确签名必不可少。 region
     * 必须与存储桶实际所在地域一致（腾讯云、七牛云会校验）。
     * </p>
     */
    @Bean
    public S3Presigner s3Presigner() {
        software.amazon.awssdk.services.s3.S3Configuration config = software.amazon.awssdk.services.s3.S3Configuration.builder()
                .pathStyleAccessEnabled(ossProperties.resolvePathStyle()).chunkedEncodingEnabled(false).build();
        return S3Presigner.builder().region(getRegion()).credentialsProvider(credentialsProvider()).endpointOverride(getUri()).serviceConfiguration(config)
                .build();
    }

    /**
     * 从配置中读取 Region。
     * <p>
     * - MinIO / 阿里云 OSS：填任意值均可，us-east-1 是常用默认值<br>
     * - 腾讯云 COS / 七牛云 Kodo：必须填写正确的地域，否则 Presigned URL 返回 403
     * </p>
     */
    private Region getRegion() {
        String region = ossProperties.getRegion();
        if (!StringUtils.hasText(region)) {
            return Region.US_EAST_1;
        }
        return Region.of(region);
    }

    private StaticCredentialsProvider credentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(ossProperties.getAccessKey(), ossProperties.getSecretKey()));
    }

    /**
     * 构造 endpoint URI。
     * <p>
     * 若 endpoint 已包含协议前缀（http:// / https://），直接使用； 否则拼接 scheme 配置。
     * </p>
     *
     * @throws IllegalStateException
     *             endpoint 未配置时抛出，提示用户检查 application.yml
     */
    private URI getUri() {
        String endpoint = ossProperties.getEndpoint();
        if (!StringUtils.hasText(endpoint)) {
            throw new IllegalStateException("oss.endpoint 未配置，请在 application.yml 中设置 oss.endpoint（如 http://127.0.0.1:9000）");
        }
        // 去掉末尾多余的斜杠，避免 URI 解析异常
        endpoint = endpoint.stripTrailing();
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        if (endpoint.startsWith("https://") || endpoint.startsWith("http://")) {
            return URI.create(endpoint);
        }
        String scheme = ossProperties.getScheme().toString();
        return URI.create(scheme + "://" + endpoint);
    }
}
