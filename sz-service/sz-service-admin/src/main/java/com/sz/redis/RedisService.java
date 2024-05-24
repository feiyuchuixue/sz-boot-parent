package com.sz.redis;

import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.UserPermissionChangeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 业务级 redis service
 */
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    /**
     * 发布Permission 变更消息
     *
     * @param message
     */
    public void sendPermissionChangeMsg(UserPermissionChangeMessage message) {
        redisTemplate.convertAndSend(GlobalConstant.CHANGE_PERMISSIONS_SIGNAL, message);
    }

}
