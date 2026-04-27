package com.sz.resource.enums;

/**
 * 资源命名规则
 *
 * <ul>
 * <li>{@link #BIZ_KEY} — 以业务标识作为文件名，同 key 重传覆盖旧文件（适合 Logo、头像等唯一资源）</li>
 * <li>{@link #UUID} — 每次上传生成随机 UUID 文件名，不覆盖（适合用户上传的普通文件）</li>
 * <li>{@link #ORIGINAL} — 保持原始文件名（需注意文件名冲突风险）</li>
 * <li>{@link #TIMESTAMP} — 时间戳文件名（yyyyMMddHHmmssSSS + 扩展名）</li>
 * </ul>
 */
public enum NamingRuleEnum {

    /**
     * 业务 Key 命名：文件名 = bizKey + 扩展名
     * <p>
     * 示例：bizKey=github → 文件名=github.svg
     * </p>
     * <p>
     * 同 bizKey 重传时覆盖旧文件
     * </p>
     */
    BIZ_KEY,

    /**
     * UUID 命名：文件名 = UUID（无连字符）+ 扩展名
     * <p>
     * 示例：文件名=550e8400e29b41d4a716446655440000.svg
     * </p>
     * <p>
     * 每次上传生成新文件，不覆盖旧文件
     * </p>
     */
    UUID,

    /**
     * 保持原始文件名
     * <p>
     * 示例：用户上传 report.pdf → 文件名=report.pdf
     * </p>
     * <p>
     * 注意：同名文件会被覆盖，需结合路径策略（如 DATE/BIZ_DATE）避免冲突
     * </p>
     */
    ORIGINAL,

    /**
     * 时间戳命名：文件名 = yyyyMMddHHmmssSSS + 扩展名
     * <p>
     * 示例：文件名=20260416143025123.pdf
     * </p>
     * <p>
     * 精确到毫秒，高并发下仍可能冲突，建议结合路径策略分散
     * </p>
     */
    TIMESTAMP
}
