package com.sz.resource.spi;

import com.sz.resource.config.ResourceSceneConfig;

import java.util.Map;

/**
 * 资源场景配置提供者（SPI 扩展点）
 *
 * <p>
 * 定义如何获取场景配置。框架默认提供基于 YML 配置文件的实现
 * ({@code YmlResourceSceneProvider})，上层业务可覆盖为数据库驱动、Redis 缓存等实现。
 *
 * <h3>扩展场景</h3>
 *
 * <h4>1. YML 默认（框架内置）</h4>
 * <p>
 * 场景配置写在 application.yml 中，启动时加载，修改需重启。
 * 
 * <pre>{@code
 * // 无需任何代码，框架自动注册 YmlResourceSceneProvider
 * // application.yml:
 * sso:
 *   resource:
 *     scenes:
 *       - code: sso.provider.logo
 *         type: LOCAL
 *         serve-mode: DIRECT
 *         path: providers/
 *         ...
 * }</pre>
 *
 * <h4>2. 数据库动态（上层覆盖）</h4>
 * <p>
 * 场景配置存储在 sys_resource_scene 表中，支持运行时动态增删改。
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     &#64;Component
 *     &#64;RequiredArgsConstructor
 *     public class DbResourceSceneProvider implements ResourceSceneProvider {
 *
 *         private final SysResourceSceneMapper mapper;
 * 
 *         // 可选：本地缓存（ConcurrentHashMap + 定时刷新）
 *         private volatile Map<String, ResourceSceneConfig> cache;
 *
 *         &#64;Override
 *         public ResourceSceneConfig getScene(String sceneCode) {
 *             return getCache().get(sceneCode);
 *         }
 *
 *         @Override
 *         public Map<String, ResourceSceneConfig> getAllScenes() {
 *             return getCache();
 *         }
 *
 *         // ... 缓存刷新逻辑
 *     }
 * }
 * </pre>
 *
 * <h4>3. Redis 缓存（上层覆盖）</h4>
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     &#64;Component
 *     &#64;RequiredArgsConstructor
 *     public class RedisResourceSceneProvider implements ResourceSceneProvider {
 *
 *         private final RedisTemplate<String, ResourceSceneConfig> redisTemplate;
 * 
 *         private static final String KEY_PREFIX = "resource:scene:";
 *
 *         &#64;Override
 *         public ResourceSceneConfig getScene(String sceneCode) {
 *             return redisTemplate.opsForValue().get(KEY_PREFIX + sceneCode);
 *         }
 *
 *         @Override
 *         public Map<String, ResourceSceneConfig> getAllScenes() {
 *             // 从 Redis Hash 或逐个 key 获取
 *         }
 *     }
 * }
 * </pre>
 *
 * <h4>4. 组合模式（上层覆盖）</h4>
 * <p>
 * 优先从 DB/Redis 查找，未命中则回退到 YML 配置。
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     &#64;Component
 *     &#64;RequiredArgsConstructor
 *     public class CompositeResourceSceneProvider implements ResourceSceneProvider {
 *
 *         private final DbResourceSceneProvider dbProvider;
 * 
 *         private final ResourceProperties resourceProperties; // YML 兜底
 *
 *         &#64;Override
 *         public ResourceSceneConfig getScene(String sceneCode) {
 *             ResourceSceneConfig scene = dbProvider.getScene(sceneCode);
 *             if (scene != null)
 *                 return scene;
 *             return resourceProperties.getSceneMap().get(sceneCode);
 *         }
 *
 *         @Override
 *         public Map<String, ResourceSceneConfig> getAllScenes() {
 *             Map<String, ResourceSceneConfig> merged = new LinkedHashMap<>(resourceProperties.getSceneMap());
 *             merged.putAll(dbProvider.getAllScenes()); // DB 优先级更高
 *             return merged;
 *         }
 *     }
 * }
 * </pre>
 *
 * @see com.sz.resource.config.ResourceSceneConfig
 */
public interface ResourceSceneProvider {

    /**
     * 根据场景编码获取配置
     *
     * @param sceneCode
     *            场景编码，如 "sso.provider.logo"
     * @return 场景配置，未找到时返回 null
     */
    ResourceSceneConfig getScene(String sceneCode);

    /**
     * 获取所有场景配置
     *
     * @return 场景编码 → 配置的映射，不为 null
     */
    Map<String, ResourceSceneConfig> getAllScenes();
}
