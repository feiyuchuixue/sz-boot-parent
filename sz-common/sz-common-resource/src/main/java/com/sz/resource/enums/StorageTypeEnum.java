package com.sz.resource.enums;

/**
 * 资源存储类型
 * <p>
 * LOCAL 由 sz-common-resource 在全局 root 下管理文件；OSS 委托 sz-common-oss 的 OssClient 读写
 * S3 兼容存储。 OSS 默认连接来自 sz.oss，场景也可提供独立连接；provider 用于寻址风格等服务商差异判定，不会自行切换
 * endpoint。
 * </p>
 */
public enum StorageTypeEnum {

    /**
     * 本地磁盘存储。 文件由资源模块写入全局 root 下，场景必须配置合法 path。
     */
    LOCAL,

    /**
     * S3 兼容对象存储。 文件读写委托 sz-common-oss，具体服务商由 OSS 连接配置决定。
     */
    OSS
}
