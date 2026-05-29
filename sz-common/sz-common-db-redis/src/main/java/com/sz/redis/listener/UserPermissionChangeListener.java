package com.sz.redis.listener;

import com.sz.core.common.entity.UserPermissionChangeMessage;
import com.sz.core.util.JsonUtils;
import com.sz.redis.handler.UserPermissionChangeMsgHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * redis消息listener, 用于监听 用户permission change
 *
 * @author sz
 * @since 2023/9/8 10:12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserPermissionChangeListener implements MessageListener {

    private final List<UserPermissionChangeMsgHandler> messageHandlers;

    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object raw = redisTemplate.getValueSerializer().deserialize(message.getBody());
        if (!(raw instanceof UserPermissionChangeMessage)) {
            log.warn("[user-permission-change] 收到非 UserPermissionChangeMessage 类型消息，已忽略, type={}", raw == null ? "null" : raw.getClass().getName());
            return;
        }
        UserPermissionChangeMessage upcm = (UserPermissionChangeMessage) raw;
        if (log.isDebugEnabled()) {
            log.debug("[user-permission-change] upcm = {}", JsonUtils.toJsonString(upcm));
        }
        for (UserPermissionChangeMsgHandler handler : messageHandlers) {
            try {
                handler.handlerMsg(upcm);
            } catch (Exception e) {
                log.error("[user-permission-change] handler 处理异常, handler={}, err={}", handler.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

}
