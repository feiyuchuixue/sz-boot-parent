package com.sz.redis.listener;

import com.sz.core.common.entity.TransferMessage;
import com.sz.core.util.JsonUtils;
import com.sz.redis.handler.ServiceToWsMsgHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * redis消息listener, 用于service to websocket 消息的推送
 *
 * @author sz
 * @since 2023/9/8 10:12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ServiceToWsListener implements MessageListener {

    private final List<ServiceToWsMsgHandler> serviceToWsMsgHandlers;

    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object raw = redisTemplate.getValueSerializer().deserialize(message.getBody());
        if (!(raw instanceof TransferMessage)) {
            log.warn("[service-to-ws] 收到非 TransferMessage 类型消息，已忽略, type={}", raw == null ? "null" : raw.getClass().getName());
            return;
        }
        TransferMessage tm = (TransferMessage) raw;
        if (log.isDebugEnabled()) {
            log.debug("[service-to-ws] tm = {}", JsonUtils.toJsonString(tm));
        }
        // 调用所有实现了 ServiceToWsMsgHandler 接口的处理器
        for (ServiceToWsMsgHandler handler : serviceToWsMsgHandlers) {
            try {
                handler.handleTransferMessage(tm);
            } catch (Exception e) {
                log.error("[service-to-ws] handler 处理异常, handler={}, err={}", handler.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

}
