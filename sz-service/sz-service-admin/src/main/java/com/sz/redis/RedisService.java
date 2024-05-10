package com.sz.redis;

import com.sz.admin.system.pojo.vo.sysdict.DictVO;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.UserPermissionChangeMessage;
import com.sz.core.util.JwtUtils;
import com.sz.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    public String getUserInfoKey(String username) {
        return StringUtils.replacePlaceholders(CommonKeyConstants.TOKEN_SESSION, username);
    }

    public void clearUserInfo(String username) {
        redisTemplate.delete(getUserInfoKey(username));
    }

    public String getAuthRefreshKey(String refreshToken) {
        return StringUtils.replacePlaceholders(RedisKeymapConstants.AUTH_REFRESH, refreshToken);
    }

    public void setAuthRefresh(String refreshToken, String accessToken) {
        String key = getAuthRefreshKey(refreshToken);
        redisTemplate.opsForValue().set(key, accessToken, JwtUtils.REFRESH_EXPIRATION, TimeUnit.SECONDS);
    }

    public boolean checkRefresh(String refreshToken) {
        String key = getAuthRefreshKey(refreshToken);
        return redisTemplate.hasKey(key);
    }

    public void clearRefreshToken(String refreshToken) {
        String key = getAuthRefreshKey(refreshToken);
        redisTemplate.delete(key);
    }

    public String getAuthRefresh(String refreshToken) {
        String key = getAuthRefreshKey(refreshToken);
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String getAuthInvalidAccessKey(String accessToken) {
        return StringUtils.replacePlaceholders(RedisKeymapConstants.AUTH_INVALID_ACCESS, accessToken);
    }

    public void setAuthInvalidAccess(String accessToken, Date expirationDate) {
        String key = getAuthInvalidAccessKey(accessToken);
        Instant now = Instant.now();
        Instant expiration = expirationDate.toInstant();
        Duration duration = Duration.between(now, expiration);
        redisTemplate.opsForValue().set(key, 1, duration);
    }

    public void setAuthInvalidAccess(String accessToken, long exp) {
        String key = getAuthInvalidAccessKey(accessToken);
        long currentTimeMillis = System.currentTimeMillis();
        // 将毫秒时间戳转换为秒
        long currentTimestampInSeconds = currentTimeMillis / 1000;
        long betweenSeconds = exp - currentTimestampInSeconds;
        redisTemplate.opsForValue().set(key, 1, Duration.ofSeconds(betweenSeconds));
    }

    public boolean checkAuthInvalidAccess(String accessToken) {
        String key = getAuthInvalidAccessKey(accessToken);
        return redisTemplate.hasKey(key);
    }

    /**
     * 发布Permission 变更消息
     *
     * @param message
     */
    public void sendPermissionChangeMsg(UserPermissionChangeMessage message) {
        redisTemplate.convertAndSend(GlobalConstant.CHANGE_PERMISSIONS_SIGNAL, message);
    }

    /**
     * 批量更新字典
     *
     * @param typeCode
     * @param dictMap
     */
    public void putAllSysDictCache(String typeCode, Map<String, List<DictVO>> dictMap) {
        redisTemplate.opsForHash().putAll(RedisKeymapConstants.SYS_DICT, dictMap);
        redisTemplate.expire(RedisKeymapConstants.SYS_DICT, 2, TimeUnit.HOURS);
    }

    /**
     * 更新字典
     *
     * @param typeCode
     * @param dictDataArr
     */
    public void putSysDictCache(String typeCode, List<DictVO> dictDataArr) {
        redisTemplate.opsForHash().put(RedisKeymapConstants.SYS_DICT, typeCode, dictDataArr);
        redisTemplate.expire(RedisKeymapConstants.SYS_DICT, 2, TimeUnit.HOURS);
    }

    /**
     * 获取指定类型的字典缓存
     *
     * @param typeCode
     * @return
     */
    public List<DictVO> getSysDictCache(String typeCode) {
        List<DictVO> dictVOS = (List<DictVO>) redisTemplate.opsForHash().get(RedisKeymapConstants.SYS_DICT, typeCode);
        return dictVOS;
    }

    /**
     * 判断指定类型的字典缓存是否存在
     *
     * @param typeCode
     * @return
     */
    public boolean existsDict(String typeCode) {
        return redisTemplate.opsForHash().hasKey(RedisKeymapConstants.SYS_DICT, typeCode);
    }

}
