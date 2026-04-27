package com.sz.resource.config;

import com.sz.resource.spi.ResourceSceneProvider;
import com.sz.resource.spi.ResourceSecurityPolicyProvider;
import com.sz.resource.spi.YmlResourceSceneProvider;
import com.sz.resource.spi.YmlSecurityPolicyProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 资源模块自动配置
 * <p>
 * 注册默认的 SPI 实现（YML 驱动），当上层业务提供了自定义实现时自动让位。
 * </p>
 */
@Configuration
public class ResourceAutoConfiguration {

    /**
     * 默认场景配置提供者：基于 YML 配置
     * <p>
     * 当容器中存在其他 {@link ResourceSceneProvider} 实现（如 DbResourceSceneProvider）时， 此 Bean
     * 不会注册。
     */
    @Bean
    @ConditionalOnMissingBean(ResourceSceneProvider.class)
    public YmlResourceSceneProvider ymlResourceSceneProvider(ResourceProperties resourceProperties) {
        return new YmlResourceSceneProvider(resourceProperties);
    }

    /**
     * 默认安全策略提供者：基于 YML 配置
     * <p>
     * 当容器中存在其他 {@link ResourceSecurityPolicyProvider} 实现时， 此 Bean 不会注册。
     */
    @Bean
    @ConditionalOnMissingBean(ResourceSecurityPolicyProvider.class)
    public YmlSecurityPolicyProvider ymlSecurityPolicyProvider(ResourceProperties resourceProperties) {
        return new YmlSecurityPolicyProvider(resourceProperties);
    }
}
