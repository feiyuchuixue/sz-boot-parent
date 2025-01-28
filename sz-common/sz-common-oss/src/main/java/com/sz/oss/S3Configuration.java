package com.sz.oss;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.net.URI;

/**
 * S3AsyncConfiguration
 * 
 * @author sz
 * @since 2024/11/12 15:16
 */
@Configuration
@RequiredArgsConstructor
public class S3Configuration {

    private final OssProperties ossProperties;

    @Bean
    public S3AsyncClient s3AsyncClient() {
        StaticCredentialsProvider credentialsProvider = credentialsProvider();
        return S3AsyncClient.crtBuilder().region(getRegion()).forcePathStyle(isPathStyle()).credentialsProvider(credentialsProvider).endpointOverride(getUri())
                .targetThroughputInGbps(20.0).checksumValidationEnabled(false).build();
    }

    @Bean
    public S3Client s3Client() {
        StaticCredentialsProvider credentialsProvider = credentialsProvider();
        return S3Client.builder().region(getRegion()).forcePathStyle(isPathStyle()).credentialsProvider(credentialsProvider).endpointOverride(getUri()).build();
    }

    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder().s3Client(s3AsyncClient).build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        software.amazon.awssdk.services.s3.S3Configuration config = software.amazon.awssdk.services.s3.S3Configuration.builder()
                .pathStyleAccessEnabled(isPathStyle()).chunkedEncodingEnabled(false).build();
        return S3Presigner.builder().region(getRegion()).credentialsProvider(credentialsProvider()).endpointOverride(getUri()).serviceConfiguration(config)
                .build();
    }

    private Region getRegion() {
        return Region.US_EAST_1;
    }

    private StaticCredentialsProvider credentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(ossProperties.getAccessKey(), ossProperties.getSecretKey()));
    }
    /**
     * 强制使用路径风格访问存储桶和对象的参数 `forcePathStyle` 在 MinIO 中的作用。
     *
     * 在 S3 兼容 API 中，访问存储桶和对象有两种方式：
     *
     * <ul>
     * <li><b>虚拟主机风格</b>：存储桶名称作为 URL 的一部分，例如：
     * <code>http://bucket-name.s3.amazonaws.com/object-name</code>。 此方式要求存储桶名称符合
     * DNS 规则。</li>
     * <li><b>路径风格</b>：存储桶名称作为 URL 路径的一部分，例如：
     * <code>http://s3.amazonaws.com/bucket-name/object-name</code>。 此方式不要求存储桶名称符合
     * DNS 规则。</li>
     * </ul>
     *
     * 使用 MinIO 作为 S3 兼容服务时，如果存储桶名称不符合 DNS 规则， 或希望强制使用路径风格的 URL，可以将 `forcePathStyle`
     * 参数设置为 `true`。 这样，即使存储桶名称不符合 DNS 规则，也可以通过路径风格访问存储桶和对象。
     *
     * <p>
     * 例如，当 `forcePathStyle` 设置为 `true` 时，访问对象的 URL 格式应为：
     * </p>
     * <ul>
     * <li><code>http://minio-server:9000/bucket-name/object-name</code></li>
     * <li>而非：<code>http://bucket-name.minio-server:9000/object-name</code></li>
     * </ul>
     *
     * 在某些客户端库或工具中，明确设置 `forcePathStyle` 参数对于确保与 MinIO 的兼容性非常重要， 特别是在存储桶名称不符合 DNS
     * 命名规则时。
     */
    private boolean isPathStyle() {
        return ossProperties.getProvider().equals(OssProviderEnum.MINIO);
    }

    private URI getUri() {
        if (ossProperties.getEndpoint().startsWith("https://") || ossProperties.getEndpoint().startsWith("http://")) {
            return URI.create(ossProperties.getEndpoint());
        }
        String scheme = ossProperties.isHttps() ? "https" : "http";
        return URI.create(scheme + "://" + ossProperties.getEndpoint());
    }
}
