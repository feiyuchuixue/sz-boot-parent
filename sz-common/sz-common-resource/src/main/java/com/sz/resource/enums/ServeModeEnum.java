package com.sz.resource.enums;

/**
 * 资源访问模式（serve-mode 三值模型）
 *
 * <p>
 * 替代原有 {@code AccessLevelEnum}（PUBLIC/PRIVATE）+
 * {@code PrivateModeEnum}（PRESIGNED/PROXY）的组合， 用单一枚举统一表达资源的访问方式。
 *
 * <ul>
 * <li>{@link #DIRECT} — 明文直接访问，base-url 指向 Java endpoint 或 Nginx（默认模式）</li>
 * <li>{@link #TOKEN} — 一次性加密 token + 平台代理读取，隐藏真实存储地址</li>
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
     * 明文直接访问（默认）
     * <p>
     * 访问 URL = base-url + 相对路径，前端直接使用。 适合公开资源（Logo、头像等）。
     */
    DIRECT,

    /**
     * 一次性加密 token + 平台代理
     * <p>
     * 上传时不生成访问 URL，由 ResourceAccessService 按需生成临时 token， 前端通过
     * /resource/token/{token} 端点访问，平台代理读取并返回文件流。 适合需要隐藏真实存储地址的场景（合同、敏感文件等）。
     */
    TOKEN,

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
