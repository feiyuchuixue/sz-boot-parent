package com.sz.platform.debounce;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisDebounceService
 * @Author sz
 * @Date 2024/9/18 10:05
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class RedisDebounceService {

    private final StringRedisTemplate redisTemplate;

    private static final String DEBOUNCE_PREFIX = "debounce:";

    // 尝试获取分布式锁
    public boolean acquireLock(String key, long debounceInterval) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        // 这里使用redis锁保证分布式场景下的稳定性
        Boolean result = ops.setIfAbsent(DEBOUNCE_PREFIX + key, "true", debounceInterval, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(result);
    }

    // 释放分布式锁
    public void releaseLock(String key) {
        redisTemplate.delete(DEBOUNCE_PREFIX + key);
    }

}
