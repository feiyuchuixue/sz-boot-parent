package com.sz.security.debounce;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 接口防抖自动配置
 * <p>
 * 由 Spring Boot AutoConfiguration 机制加载（注册于
 * META-INF/spring/...AutoConfiguration.imports）。 条件：classpath 上存在
 * StringRedisTemplate（即引入了 Redis 相关依赖）时自动生效。 可通过 sz.debounce.enabled=false
 * 关闭防抖功能（切面仍注册，但内部直接放行）。
 * </p>
 *
 * @author sz
 * @since 2024/9/18
 * @version 1.0
 */
@Configuration
@ConditionalOnClass(StringRedisTemplate.class)
@EnableConfigurationProperties(DebounceProperties.class)
public class DebounceAutoConfiguration {

    /**
     * 注册 Redis 防抖锁服务
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisDebounceService redisDebounceService(StringRedisTemplate stringRedisTemplate) {
        return new RedisDebounceService(stringRedisTemplate);
    }

    /**
     * 注册防抖切面
     * <p>
     * 依赖 RedisDebounceService、HttpServletRequest（由 Spring MVC
     * 注入请求代理）、DebounceProperties。
     * </p>
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sz.debounce", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DebounceAspect debounceAspect(RedisDebounceService redisDebounceService, jakarta.servlet.http.HttpServletRequest request,
            DebounceProperties debounceProperties) {
        return new DebounceAspect(redisDebounceService, request, debounceProperties);
    }

}
