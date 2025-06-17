package com.sz.platform.handler;

import com.sz.core.common.entity.TransferMessage;
import com.sz.core.util.JsonUtils;
import com.sz.redis.handler.WsToServiceMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebsocketMessageHandler implements WsToServiceMsgHandler {

    @Override
    public void handlerMsg(TransferMessage transferMessage) {
        // [do something ...] 在业务层接收透传过来的 websocket信息，进行业务处理
        log.info(" [WsToService] transferMessage = {}", JsonUtils.toJsonString(transferMessage));
    }

}
