package com.sz.resource.util;

/**
 * objectKey 工具类：业务逻辑 key 与 OSS SDK 物理 key 的互转
 *
 * <p>
 * 业务侧统一使用"含 bucket 前缀的逻辑 objectKey"（如
 * {@code client-logos/20260418/bg8.png}），以便入库、URL 还原、跨场景语义一致。 调用 OSS SDK 时需剥离
 * bucket 段，得到物理 key（如 {@code 20260418/bg8.png}）， 否则会出现
 * {@code bucket/bucket/...} 的错误路径。
 * </p>
 *
 * <p>
 * 所有需要"逻辑 key → 物理 key"的场景（upload / download / presign 等）必须统一复用
 * 本工具类，避免在多处重复实现导致不一致。
 * </p>
 */
public final class ObjectKeyUtils {

    private ObjectKeyUtils() {
    }

    /**
     * 业务逻辑 objectKey 转换为 OSS SDK 物理 key
     * <p>
     * 若 logicalKey 以 {@code "{bucket}/"} 开头则剥离前缀，否则原样返回（容错兼容旧数据）。
     * </p>
     *
     * @param logicalKey
     *            业务层 objectKey，形如 {bucket}/{subPath}/{filename}
     * @param bucket
     *            OSS 桶名
     * @return OSS SDK 使用的物理 key，形如 {subPath}/{filename}
     */
    public static String toPhysicalKey(String logicalKey, String bucket) {
        if (logicalKey == null || bucket == null || bucket.isBlank()) {
            return logicalKey;
        }
        String prefix = bucket.endsWith("/") ? bucket : bucket + "/";
        return logicalKey.startsWith(prefix) ? logicalKey.substring(prefix.length()) : logicalKey;
    }
}
