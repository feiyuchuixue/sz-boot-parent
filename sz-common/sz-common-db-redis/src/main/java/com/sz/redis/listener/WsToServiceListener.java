package com.sz.redis.listener;

import com.sz.core.common.entity.TransferMessage;
import com.sz.core.util.JsonUtils;
import com.sz.redis.handler.WsToServiceMsgHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * redis消息listener, 用于监听websocket to service消息的推送
 *
 * @author sz
 * @date 2023/9/8 10:12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WsToServiceListener implements MessageListener {

    private final List<WsToServiceMsgHandler> messageHandlers;

    private final RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        TransferMessage tm = (TransferMessage) redisTemplate.getValueSerializer().deserialize(message.getBody());
        log.info(" [ws-to-service] tm = " + JsonUtils.toJsonString(tm));
        // 调用所有实现了TransferMessageHandler接口的处理器
        for (WsToServiceMsgHandler handler : messageHandlers) {
            handler.handlerMsg(tm);
        }
    }

}
