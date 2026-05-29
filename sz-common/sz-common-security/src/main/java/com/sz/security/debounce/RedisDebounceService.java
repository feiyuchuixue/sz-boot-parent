package com.sz.security.debounce;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的接口防抖锁服务
 * <p>
 * 使用 Redis SET NX + TTL 实现分布式防重锁，保证集群部署下的防抖稳定性。
 * </p>
 *
 * @author sz
 * @since 2024/9/18 10:05
 * @version 1.0
 */
@RequiredArgsConstructor
public class RedisDebounceService {

    private final StringRedisTemplate redisTemplate;

    private static final String DEBOUNCE_PREFIX = "debounce:";

    /**
     * 尝试获取防抖锁
     *
     * @param key
     *            锁 key（由请求特征生成）
     * @param debounceInterval
     *            锁定时长（ms）
     * @return true 表示获取成功（本次请求允许执行），false 表示已被锁定（防抖拦截）
     */
    public boolean acquireLock(String key, long debounceInterval) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Boolean result = ops.setIfAbsent(DEBOUNCE_PREFIX + key, "true", debounceInterval, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 主动释放防抖锁（一般无需手动调用，TTL 到期自动释放）
     *
     * @param key
     *            锁 key
     */
    public void releaseLock(String key) {
        redisTemplate.delete(DEBOUNCE_PREFIX + key);
    }

}
