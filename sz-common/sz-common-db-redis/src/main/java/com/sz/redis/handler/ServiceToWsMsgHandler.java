package com.sz.redis.handler;

import com.sz.core.common.entity.TransferMessage;

/**
 * 服务端 -》 到 websocket 方向，消息监听接口；具体业务可以实现此接口，进行实际处理
 */
public interface ServiceToWsMsgHandler {

    void handleTransferMessage(TransferMessage transferMessage);
}