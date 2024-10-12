package com.sz.redis.handler;

import com.sz.core.common.entity.TransferMessage;

/**
 * websocket 到 服务端 方向，消息监听接口；具体业务可以实现此接口，进行实际处理
 */
public interface WsToServiceMsgHandler {

    void handlerMsg(TransferMessage transferMessage);
}