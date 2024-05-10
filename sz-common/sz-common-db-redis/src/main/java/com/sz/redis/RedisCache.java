package com.sz.redis;

import com.sz.core.common.entity.DictCustomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisCache
 * @Author sz
 * @Date 2024/1/8 15:38
 * @Version 1.0
 */
@Component
@RequiredArgsConstructor
public class RedisCache {

    private final RedisTemplate redisTemplate;

    // ---------------sys_dict相关----------------
    public void setDict(String dictType, List<DictCustomVO> list) {
        redisTemplate.opsForHash().put(CommonKeyConstants.SYS_DICT, dictType, list);
        redisTemplate.expire(CommonKeyConstants.SYS_DICT, 2, TimeUnit.HOURS);
    }

    public void putAllDict(Map<String, List<DictCustomVO>> dictMap) {
        redisTemplate.opsForHash().putAll(CommonKeyConstants.SYS_DICT, dictMap);
        redisTemplate.expire(CommonKeyConstants.SYS_DICT, 2, TimeUnit.HOURS);
    }

    public Map<String, List<DictCustomVO>> getAllDict() {
        return redisTemplate.opsForHash().entries(CommonKeyConstants.SYS_DICT);
    }

    public List<DictCustomVO> getDictByType(String dictType) {
        if (hasHashKey(dictType)) {
            return (List<DictCustomVO>) redisTemplate.opsForHash().get(CommonKeyConstants.SYS_DICT, dictType);
        } else {
            return new ArrayList<>();
        }
    }

    public boolean hasKey() {
        return redisTemplate.hasKey(CommonKeyConstants.SYS_DICT);
    }

    public boolean hasHashKey(String dictType) {
        return redisTemplate.opsForHash().hasKey(CommonKeyConstants.SYS_DICT, dictType);
    }

    public void clearDict(String dictType) {
        redisTemplate.opsForHash().delete(CommonKeyConstants.SYS_DICT, dictType);
    }

    public void clearDictAll() {
        redisTemplate.delete(CommonKeyConstants.SYS_DICT);
    }

    // ---------------sys_config相关----------------
    public boolean hasConfKey(String key) {
        return redisTemplate.opsForHash().hasKey(CommonKeyConstants.SYS_CONFIG, key);
    }

    public String getConfValue(String key) {
        return (String) redisTemplate.opsForHash().get(CommonKeyConstants.SYS_CONFIG, key);
    }

    public void putConf(String key, String value) {
        redisTemplate.opsForHash().put(CommonKeyConstants.SYS_CONFIG, key, value);
        redisTemplate.expire(CommonKeyConstants.SYS_DICT, 2, TimeUnit.HOURS);
    }

    public void clearConf(String key) {
        redisTemplate.opsForHash().delete(CommonKeyConstants.SYS_CONFIG, key);
    }

    // ---------------sys_user用户认证相关----------------

    public long countPwdErr(String username) {
        String key = RedisUtils.getKey(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        Long increment = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return increment;
    }


}
