package com.sz.redis;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author sz
 * @since 2024/2/29 9:47
 */
public interface RedisTemplateClient {

    RedisTemplate<Object, Object> getTemplate();
}
