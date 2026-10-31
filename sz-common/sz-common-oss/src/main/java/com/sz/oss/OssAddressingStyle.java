package com.sz.oss;

/**
 * S3 请求寻址风格。
 */
public enum OssAddressingStyle {

    /**
     * 路径风格寻址。 bucket 位于 URL 路径中，地址形如 endpoint/bucket/object。
     */
    PATH,

    /**
     * 虚拟主机风格寻址。 bucket 位于主机名中，地址形如 bucket.endpoint/object。
     */
    VIRTUAL_HOSTED;

    public boolean isPathStyle() {
        return this == PATH;
    }
}
