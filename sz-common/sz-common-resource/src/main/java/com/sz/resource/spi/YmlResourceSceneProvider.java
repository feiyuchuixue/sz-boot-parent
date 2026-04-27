package com.sz.resource.spi;

import com.sz.resource.config.ResourceProperties;
import com.sz.resource.config.ResourceSceneConfig;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 基于 YML 配置的场景提供者（框架默认实现）
 *
 * <p>
 * 从 {@link ResourceProperties#getSceneMap()} 读取场景配置。 当容器中没有其他
 * {@link ResourceSceneProvider} 实现时自动注册。
 * <p>
 * 上层业务如需数据库驱动或 Redis 缓存，只需实现 {@link ResourceSceneProvider} 并注册为 Spring
 * Bean，此默认实现会自动让位。
 *
 * <p>
 * 注册方式：通过 {@link com.sz.resource.config.ResourceAutoConfiguration} 中的
 * {@code @Bean @ConditionalOnMissingBean} 方法注册，而非直接标注 {@code @Component}。
 */
@RequiredArgsConstructor
public class YmlResourceSceneProvider implements ResourceSceneProvider {

    private final ResourceProperties resourceProperties;

    @Override
    public ResourceSceneConfig getScene(String sceneCode) {
        return resourceProperties.getSceneMap().get(sceneCode);
    }

    @Override
    public Map<String, ResourceSceneConfig> getAllScenes() {
        return resourceProperties.getSceneMap();
    }
}
