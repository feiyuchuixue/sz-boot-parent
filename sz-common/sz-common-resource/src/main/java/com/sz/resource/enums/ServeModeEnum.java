package com.sz.resource.enums;

/**
 * 资源访问模式（serve-mode 三值模型）
 *
 * <p>
 * 替代原有 {@code AccessLevelEnum}（PUBLIC/PRIVATE）+
 * {@code PrivateModeEnum}（PRESIGNED/PROXY）的组合， 用单一枚举统一表达资源的访问方式。
 *
 * <ul>
 * <li>{@link #PROTECTED} — 受保护资源，不生成访问 URL，由业务接口鉴权后流式读取（默认模式）</li>
 * <li>{@link #DIRECT} — 明文直接访问，base-url 指向 Java endpoint 或 Nginx</li>
 * <li>{@link #PRESIGNED} — S3 Presigned URL，仅 OSS 存储可用</li>
 * </ul>
 *
 * <h3>DIRECT 模式的 Java/Nginx 切换</h3>
 * <p>
 * 完全由 {@code base-url} 的值隐式决定：
 * <ul>
 * <li>base-url 指向 Java 应用接口 → 由 ResourceFileController 流式响应</li>
 * <li>base-url 指向 Nginx 静态资源地址 → 由 Nginx 直接响应，Java 不参与</li>
 * </ul>
 */
public enum ServeModeEnum {

    /**
     * 明文直接访问（显式公开）
     * <p>
     * 访问 URL = base-url + 相对路径，前端直接使用。 适合公开资源（Logo、头像等）。
     */
    DIRECT,

    /**
     * 受保护业务访问（默认）
     * <p>
     * 上传和查询时不生成访问 URL。前端通过业务固定下载/预览接口访问，由业务权限、数据范围和记录—资源关系共同鉴权，
     * 再由平台流式返回文件，不签发 ticket 或临时 token。
     */
    PROTECTED,

    /**
     * S3 Presigned URL
     * <p>
     * 仅 OSS 存储可用（LOCAL 存储配 PRESIGNED 将在启动校验时报错）。 由 OssClient 生成带签名的临时 URL，有效期由场景
     * {@code expire} 配置决定（秒）。
     * </p>
     * <p>
     * 调用方无需感知：{@link com.sz.resource.service.ResourceService#resolveUrl} 会自动为
     * PRESIGNED 场景生成 URL，与 DIRECT 模式对调用方等价。 适合 MinIO / S3 私有 bucket 上的图片预览类场景。
     * </p>
     */
    PRESIGNED
}
