package com.sz.resource.spi;

import com.sz.resource.config.ResourceProperties;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * 基于 YML 配置的安全策略提供者（框架默认实现）
 *
 * <p>
 * 从 {@link ResourceProperties#getSecurity()} 读取安全策略配置， 未配置的项使用
 * {@link com.sz.resource.config.ResourceSecurityDefaults} 中的内置默认值。
 * <p>
 * 当容器中没有其他 {@link ResourceSecurityPolicyProvider} 实现时自动注册。
 *
 * <p>
 * 注册方式：通过 {@link com.sz.resource.config.ResourceAutoConfiguration} 中的
 * {@code @Bean @ConditionalOnMissingBean} 方法注册，而非直接标注 {@code @Component}。
 */
@RequiredArgsConstructor
public class YmlSecurityPolicyProvider implements ResourceSecurityPolicyProvider {

    private final ResourceProperties resourceProperties;

    @Override
    public Set<String> getAllowedExts() {
        return resourceProperties.getSecurity().getEffectiveAllowedExts();
    }

    @Override
    public Set<String> getAllowedMimeTypes() {
        return resourceProperties.getSecurity().getEffectiveAllowedMimeTypes();
    }

    @Override
    public long getMaxSizeBytes() {
        return resourceProperties.getSecurity().getEffectiveMaxSizeBytes();
    }
}
