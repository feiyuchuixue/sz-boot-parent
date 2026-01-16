package com.sz.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sz
 * @since 2024/11/12 14:39
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    // 服务提供商
    private OssProviderEnum provider = OssProviderEnum.MINIO;

    // 访问站点
    private String endpoint;

    // access_key
    private String accessKey;

    // secret_key
    private String secretKey;

    // 存储空间
    private String bucketName;

    // 富文本存储空间
    private String richtextBucketName;

    // 域名
    private String domain;

    // 文件名称方式
    private FileNamingEnum naming = FileNamingEnum.ORIGINAL;

    private SchemeEnum scheme = SchemeEnum.https;

}
