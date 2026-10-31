package com.sz.resource.enums;

/**
 * 资源访问模式（serve-mode 三值模型）
 *
 * <p>
 * 替代原有 {@code AccessLevelEnum}（PUBLIC/PRIVATE）+
 * {@code PrivateModeEnum}（PRESIGNED/PROXY）的组合， 用单一枚举统一表达资源的访问方式。
 *
 * 各候选值的访问行为和适用限制见对应枚举常量说明。
 *
 * <h3>DIRECT 模式的 Java/Nginx 切换</h3>
 * <p>
 * 完全由 {@code base-url} 的值隐式决定：
 * <ul>
 * <li>base-url 指向业务已实现并开放的 Java 接口 → 由该接口流式响应</li>
 * <li>base-url 指向已配置的 Nginx 静态资源地址 → 由 Nginx 直接响应，Java 不参与</li>
 * </ul>
 * 本枚举只控制地址生成，不会自动创建上述路由。
 */
public enum ServeModeEnum {

    /**
     * 生成直连地址。 ResourceService 仅拼接 base-url 与相对路径，不修改 OSS bucket 或 CDN
     * 权限，也不保证对应应用或代理路由已开放；地址能否 访问由存储服务、反向代理和业务路由配置决定。
     */
    DIRECT,

    /**
     * 不生成访问地址。 ResourceService 仍提供文件流读取能力，但不自动鉴权；业务下载或预览接口必须在读取前自行校验登录、权限、数据范围及记录
     * 与资源的归属关系。
     */
    PROTECTED,

    /**
     * 生成 S3 临时签名地址。 仅 OSS 存储可用，LOCAL 组合会在启动校验时报错，有效期由场景 expire 秒数决定。ResourceService
     * 不判断请求者是否有权 取得该地址，业务应在签发前完成鉴权；持有者可在地址过期前使用。
     */
    PRESIGNED
}
