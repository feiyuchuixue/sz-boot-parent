package com.sz.redis.handler;

import com.sz.core.common.entity.TransferMessage;
/**
 * 服务端到 WebSocket 方向的消息监听接口，允许具体业务实现此接口以进行消息的处理。
 * <p>
 * 该接口为服务端向 WebSocket 客户端推送消息提供了基础的监听机制，业务可以通过实现该接口， 自定义如何处理接收到的 WebSocket 消息。
 *
 * @since 2023-12-08
 */

public interface ServiceToWsMsgHandler {

    void handleTransferMessage(TransferMessage transferMessage);
}