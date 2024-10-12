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
 * @date 2023/9/8 10:12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserPermissionChangeListener implements MessageListener {

    private final List<UserPermissionChangeMsgHandler> messageHandlers;

    private final RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UserPermissionChangeMessage upcm = (UserPermissionChangeMessage) redisTemplate.getValueSerializer().deserialize(message.getBody());
        log.info(" [user change permission ] tm = " + JsonUtils.toJsonString(upcm));
        // 调用所有实现了TransferMessageHandler接口的处理器
        for (UserPermissionChangeMsgHandler handler : messageHandlers) {
            handler.handlerMsg(upcm);
        }
    }

}
