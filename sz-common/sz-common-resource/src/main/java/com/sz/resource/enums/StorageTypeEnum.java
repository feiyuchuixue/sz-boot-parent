package com.sz.resource.enums;

/**
 * 资源存储类型
 * <p>
 * LOCAL - 本地磁盘存储，由 sz-common-resource 自身管理读写 OSS - 对象存储，委托给 sz-common-oss 的
 * OssClient 处理； 具体厂商（MinIO / 阿里云 / 七牛 / 腾讯云等）由 oss.* 配置决定，本模块不感知
 * </p>
 */
public enum StorageTypeEnum {

    /** 本地磁盘存储 */
    LOCAL,

    /** 对象存储（S3 兼容，底层由 sz-common-oss 驱动，厂商透明） */
    OSS
}
