package com.sz.oss;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

/**
 * S3 客户端配置
 * <p>
 * 初始化默认连接的 S3AsyncClient、S3Client、S3Presigner、S3TransferManager，并按资源场景创建受管客户端。
 * 默认连接参数来自 {@link OssProperties}；场景参数可覆盖端点、凭据、region、provider 和寻址风格。 支持
 * MinIO、RustFS、阿里云 OSS、腾讯云 COS、七牛云 Kodo 等 S3 兼容存储。
 * </p>
 *
 * @author sz
 * @since 2024/11/12 15:16
 */
@Configuration
@ConditionalOnProperty(prefix = "sz.oss", name = "endpoint")
@RequiredArgsConstructor
public class S3Configuration {

    private final OssProperties ossProperties;

    /**
     * 异步 S3 客户端（基于 AWS CRT）。
     * <p>
     * 此处关闭 checksum 校验，并使用生效的 endpoint、region 和寻址风格创建客户端。
     * </p>
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        StaticCredentialsProvider credentialsProvider = credentialsProvider();
        return S3AsyncClient.crtBuilder().region(getRegion()).forcePathStyle(ossProperties.resolvePathStyle()).credentialsProvider(credentialsProvider)
                .endpointOverride(ossProperties.resolveEndpointUri()).targetThroughputInGbps(20.0).checksumValidationEnabled(false).build();
    }

    @Bean
    public S3Client s3Client() {
        StaticCredentialsProvider credentialsProvider = credentialsProvider();
        return S3Client.builder().region(getRegion()).forcePathStyle(ossProperties.resolvePathStyle()).credentialsProvider(credentialsProvider)
                .endpointOverride(ossProperties.resolveEndpointUri()).build();
    }

    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder().s3Client(s3AsyncClient).build();
    }

    /**
     * Presigned URL 生成器。
     * <p>
     * 使用生效的 endpoint、region、凭据和寻址风格构建，并关闭分块编码。
     * </p>
     */
    @Bean
    public S3Presigner s3Presigner() {
        software.amazon.awssdk.services.s3.S3Configuration config = software.amazon.awssdk.services.s3.S3Configuration.builder()
                .pathStyleAccessEnabled(ossProperties.resolvePathStyle()).chunkedEncodingEnabled(false).build();
        return S3Presigner.builder().region(getRegion()).credentialsProvider(credentialsProvider()).endpointOverride(ossProperties.resolveEndpointUri())
                .serviceConfiguration(config).build();
    }

    @Bean
    public OssClientRegistry ossClientRegistry(OssClient defaultClient) {
        return new OssClientRegistry(ossProperties, defaultClient, this::createManagedClient);
    }

    private OssClientRegistry.ManagedClient createManagedClient(OssSceneConnection connection, OssAddressingStyle addressingStyle) {
        StaticCredentialsProvider credentials = StaticCredentialsProvider
                .create(AwsBasicCredentials.create(connection.resolveAccessKey(ossProperties), connection.resolveSecretKey(ossProperties)));
        Region region = Region.of(connection.resolveRegion(ossProperties));
        boolean pathStyle = addressingStyle.isPathStyle();
        S3AsyncClient asyncClient = null;
        S3Client client = null;
        S3TransferManager transferManager = null;
        S3Presigner presigner = null;
        try {
            asyncClient = S3AsyncClient.crtBuilder().region(region).forcePathStyle(pathStyle).credentialsProvider(credentials)
                    .endpointOverride(connection.resolveEndpointUri(ossProperties)).targetThroughputInGbps(20.0).checksumValidationEnabled(false).build();
            client = S3Client.builder().region(region).forcePathStyle(pathStyle).credentialsProvider(credentials)
                    .endpointOverride(connection.resolveEndpointUri(ossProperties)).build();
            transferManager = S3TransferManager.builder().s3Client(asyncClient).build();
            software.amazon.awssdk.services.s3.S3Configuration config = software.amazon.awssdk.services.s3.S3Configuration.builder()
                    .pathStyleAccessEnabled(pathStyle).chunkedEncodingEnabled(false).build();
            presigner = S3Presigner.builder().region(region).credentialsProvider(credentials).endpointOverride(connection.resolveEndpointUri(ossProperties))
                    .serviceConfiguration(config).build();
            OssClient ossClient = new OssClient(connection, ossProperties, addressingStyle, client, transferManager, presigner, asyncClient);
            S3TransferManager managedTransferManager = transferManager;
            S3Presigner managedPresigner = presigner;
            S3Client managedClient = client;
            S3AsyncClient managedAsyncClient = asyncClient;
            return new OssClientRegistry.ManagedClient(ossClient,
                    () -> closeManagedResources(managedTransferManager, managedPresigner, managedClient, managedAsyncClient));
        } catch (RuntimeException creationFailure) {
            try {
                closeManagedResources(transferManager, presigner, client, asyncClient);
            } catch (RuntimeException closeFailure) {
                creationFailure.addSuppressed(closeFailure);
            }
            throw creationFailure;
        }
    }

    private void closeManagedResources(AutoCloseable... resources) {
        RuntimeException failure = null;
        for (AutoCloseable resource : resources) {
            if (resource == null) {
                continue;
            }
            try {
                resource.close();
            } catch (Exception e) {
                RuntimeException wrapped = e instanceof RuntimeException runtimeException ? runtimeException : new IllegalStateException(e);
                if (failure == null) {
                    failure = wrapped;
                } else {
                    failure.addSuppressed(wrapped);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * 从配置中读取 S3 签名 Region；云厂商场景应与目标 bucket 的实际区域保持一致。
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

}
