package com.sz.resource.spi;

import java.util.Set;

/**
 * 资源安全策略提供者（SPI 扩展点）
 *
 * <p>
 * 定义全局安全策略（允许的扩展名、MIME 类型、最大文件大小）的获取方式。 框架默认提供基于 YML 配置的实现
 * ({@code YmlSecurityPolicyProvider})， 上层业务可覆盖为数据库驱动、配置中心等实现。
 *
 * <h3>扩展场景</h3>
 *
 * <h4>1. YML 默认（框架内置）</h4>
 * <p>
 * 从 {@code sso.resource.security} 配置段读取，未配置则使用
 * {@link com.sz.resource.config.ResourceSecurityDefaults} 中的内置默认值。
 * 
 * <pre>{@code
 * // 无需任何代码，框架自动注册 YmlSecurityPolicyProvider
 * // application.yml:
 * sso:
 *   resource:
 *     security:
 *       allowed-exts: [jpg, jpeg, png, gif, pdf]
 *       allowed-mime-types: [image/jpeg, image/png]
 *       max-size: 50MB
 * }</pre>
 *
 * <h4>2. 数据库动态（上层覆盖）</h4>
 * <p>
 * 从 sys_config 表或独立安全策略表中读取，支持运行时修改。
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     &#64;Component
 *     &#64;RequiredArgsConstructor
 *     public class DbSecurityPolicyProvider implements ResourceSecurityPolicyProvider {
 *
 *         private final SysConfigService configService;
 *
 *         &#64;Override
 *         public Set<String> getAllowedExts() {
 *             String val = configService.getConfigValue("resource.security.allowed-exts");
 *             return val != null ? Set.of(val.split(",")) : ResourceSecurityDefaults.DEFAULT_ALLOWED_EXTS;
 *         }
 *
 *         &#64;Override
 *         public Set<String> getAllowedMimeTypes() {
 *             String val = configService.getConfigValue("resource.security.allowed-mime-types");
 *             return val != null ? Set.of(val.split(",")) : ResourceSecurityDefaults.DEFAULT_ALLOWED_MIMES;
 *         }
 *
 *         @Override
 *         public long getMaxSizeBytes() {
 *             String val = configService.getConfigValue("resource.security.max-size");
 *             return val != null ? DataSize.parse(val).toBytes() : ResourceSecurityDefaults.DEFAULT_MAX_SIZE_BYTES;
 *         }
 *     }
 * }
 * </pre>
 *
 * <h4>3. 配置中心（上层覆盖）</h4>
 * <p>
 * 从 Nacos/Apollo 等配置中心获取，支持热更新。
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     &#64;Component
 *     @RefreshScope
 *     public class NacosSecurityPolicyProvider implements ResourceSecurityPolicyProvider {
 * 
 *         &#64;Value("${resource.security.allowed-exts}")
 *         private String allowedExtsConfig;
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @see com.sz.resource.config.ResourceSecurityDefaults
 */
public interface ResourceSecurityPolicyProvider {

    /**
     * 获取全局允许的文件扩展名白名单（小写，无点）
     *
     * @return 扩展名集合，不为 null
     */
    Set<String> getAllowedExts();

    /**
     * 获取全局允许的 MIME 类型白名单
     *
     * @return MIME 类型集合，不为 null
     */
    Set<String> getAllowedMimeTypes();

    /**
     * 获取全局最大文件大小（字节）
     *
     * @return 最大文件大小（字节数）
     */
    long getMaxSizeBytes();
}
