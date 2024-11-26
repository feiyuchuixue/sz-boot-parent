package com.sz.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName OssProperties
 * @Author sz
 * @Date 2024/11/12 14:39
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    {
        this.provider = OssProviderEnum.MINIO;
        this.isHttps = true;
        this.naming = FileNamingEnum.ORIGINAL;
    }

    // 服务提供商
    private OssProviderEnum provider;

    // 访问站点
    private String endpoint;

    // access_key
    private String accessKey;

    // secret_key
    private String secretKey;

    // 存储空间
    private String bucketName;

    // 域名
    private String domain;

    // 是否使用https
    private Boolean isHttps;

    // 文件名称方式
    private FileNamingEnum naming;

}
