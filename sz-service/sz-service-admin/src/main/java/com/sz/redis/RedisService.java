package com.sz.redis;

import com.sz.admin.system.pojo.vo.sysdict.DictVO;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.UserPermissionChangeMessage;
import com.sz.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
