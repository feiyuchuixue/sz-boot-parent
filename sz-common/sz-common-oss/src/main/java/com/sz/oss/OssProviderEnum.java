package com.sz.oss;

/**
 * @author sz
 * @since 2024/11/12 14:49
 */
public enum OssProviderEnum {

    /**
     * 阿里云 OSS。 使用 S3 兼容 API，未显式配置寻址风格时采用 VIRTUAL_HOSTED。
     */
    ALIYUN,

    /**
     * 七牛云 Kodo。 使用 S3 兼容 API，未显式配置寻址风格时采用 VIRTUAL_HOSTED。
     */
    QINIU,

    /**
     * 腾讯云 COS。 使用 S3 兼容 API，未显式配置寻址风格时采用 VIRTUAL_HOSTED。
     */
    TENCENT,

    /**
     * MinIO 对象存储。 适合自建 S3 兼容服务，未显式配置寻址风格时采用 PATH。
     */
    MINIO,

    /**
     * RustFS 对象存储。 适合自建 S3 兼容服务，未显式配置寻址风格时采用 PATH。
     */
    RUSTFS
}
