package com.sz.platform.handler;

import com.sz.core.common.entity.TransferMessage;
import com.sz.core.util.JsonUtils;
import com.sz.redis.handler.WsToServiceMsgHandler;
import org.springframework.stereotype.Component;

@Component
public class WebsocketMessageHandler implements WsToServiceMsgHandler {

    @Override
    public void handlerMsg(TransferMessage transferMessage) {
        // todo 在业务层接收透传过来的 websocket信息，进行业务处理
        System.out.println(" [WsToService] transferMessage = " + JsonUtils.toJsonString(transferMessage));
    }

}
