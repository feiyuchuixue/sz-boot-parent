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
 * @ClassName S3AsyncConfiguration
 * @Author sz
 * @Date 2024/11/12 15:16
 * @Version 1.0
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

    private boolean isPathStyle() {
        /**
         * orcePathStyle参数在MinIO中的作用是强制使用路径风格的请求来访问存储桶和对象。在S3兼容的API中，有两种方式来指定存储桶和对象的名称：
         *
         * 虚拟主机风格：在这种风格中，存储桶名称是URL的一部分，例如http://bucket-name.s3.amazonaws.com/object-name。这种方式要求存储桶名称必须符合DNS名称的规则。
         *
         * 路径风格：在这种风格中，存储桶名称是URL路径的一部分，例如http://s3.amazonaws.com/bucket-name/object-name。这种方式不要求存储桶名称符合DNS名称规则。
         *
         * 当您使用MinIO作为S3兼容的服务时，如果您的存储桶名称不符合DNS名称规则，或者您希望确保客户端总是使用路径风格的URL，您可以设置forcePathStyle参数为true。这样，即使您的存储桶名称不符合DNS规则，您也可以通过路径风格来访问存储桶和对象。
         *
         * 例如，如果您的MinIO服务配置了forcePathStyle为true，那么您应该使用如下URL格式来访问对象：
         *
         * http://minio-server:9000/bucket-name/object-name 而不是：
         *
         * http://bucket-name.minio-server:9000/object-name
         * 在一些客户端库或工具中，您可能需要明确设置forcePathStyle参数来确保使用路径风格的URL。这对于确保与MinIO的兼容性非常重要，特别是当您的存储桶名称不符合DNS命名规则时
         */
        return ossProperties.getProvider().equals(OssProviderEnum.MINIO);
    }

    private URI getUri() {
        if (ossProperties.getEndpoint().startsWith("https://") || ossProperties.getEndpoint().startsWith("http://")) {
            return URI.create(ossProperties.getEndpoint());
        }
        String scheme = ossProperties.getIsHttps() ? "https" : "http";
        return URI.create(scheme + "://" + ossProperties.getEndpoint());
    }
}
