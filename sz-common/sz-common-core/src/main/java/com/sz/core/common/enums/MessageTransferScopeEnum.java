package com.sz.core.common.enums;

/**
 * MessageTransferScopeEnum - 定义消息传输范围的枚举类。
 *
 * @version 1.0
 * @since 2024-02-17
 * @author sz
 */
public enum MessageTransferScopeEnum {
    // 后台服务端
    SERVER,
    // socket服务端
    SOCKET_SERVER,
    // socket客户端 （web、app等）
    SOCKET_CLIENT,

}
