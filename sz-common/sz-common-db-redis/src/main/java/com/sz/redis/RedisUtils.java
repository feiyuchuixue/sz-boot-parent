package com.sz.redis;

import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisUtil
 * @Author sz
 * @Date 2024/2/29 9:28
 * @Version 1.0
 */
public class RedisUtils {

    private static final RedisTemplate TEMPLATE = SpringApplicationContextUtils.getBean(RedisTemplateClient.class).getTemplate();

    public static String getKey(String constant, String... value) {
        return StringUtils.getRealKey(constant, value);
    }

    public static boolean hasKey(String constant, String... value) {
        return TEMPLATE.hasKey(getKey(constant, value));
    }

    public static boolean hasKey(String key) {
        return TEMPLATE.hasKey(key);
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

    public static RedisTemplate getRestTemplate() {
        return TEMPLATE;
    }

}
