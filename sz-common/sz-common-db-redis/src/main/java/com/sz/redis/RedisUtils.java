package com.sz.redis;

import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author sz
 * @since 2024/2/29 9:28
 */
@Slf4j
public class RedisUtils {

    private RedisUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final RedisTemplate<Object, Object> TEMPLATE = SpringApplicationContextUtils.getInstance().getBean(RedisTemplateClient.class).getTemplate();

    public static String getKey(String constant, String... value) {
        return StringUtils.getRealKey(constant, value);
    }

    // 检查完整键是否存在
    public static boolean hasKey(String constant, String... value) {
        // 获取完整的键
        String key = getKey(constant, value);
        return hasKey(key);
    }

    // 检查键是否存在
    public static boolean hasKey(String key) {
        // 检查 redisTemplate 是否为 null
        if (TEMPLATE == null) {
            log.error("RedisTemplate is null, cannot check key existence");
            return false;
        }

        // 调用 hasKey 方法并检查返回值是否为 null
        Boolean hasKey = TEMPLATE.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }

    public static void removeKey(String key) {
        TEMPLATE.delete(key);
    }

    public static void removeKey(String constant, String... value) {
        TEMPLATE.delete(getKey(constant, value));
    }

    public static void expire(String constant, long timeout, TimeUnit unit, String... value) {
        TEMPLATE.expire(getKey(constant, value), timeout, unit);
    }

    public static void expire(String key, long timeout, TimeUnit unit) {
        TEMPLATE.expire(key, timeout, unit);
    }

    public static Object getValue(String constant, String... value) {
        return TEMPLATE.opsForValue().get(getKey(constant, value));
    }

    public static Object getValue(String key) {
        return TEMPLATE.opsForValue().get(key);
    }

    public static RedisTemplate<Object, Object> getRestTemplate() {
        return TEMPLATE;
    }

}
