package com.sz.core.common.enums;

import com.sz.core.common.exception.common.BusinessExceptionCustomAssert;

/**
 * 异常枚举类
 */
public enum CommonResponseEnum implements BusinessExceptionCustomAssert {
    VALID_ERROR(100, "参数校验异常"),
    FILE_NOT_EXISTS(101, "文件不存在"),
    FILE_UPLOAD_EXT_ERROR(102, "上传文件类型错误"),
    FILE_UPLOAD_SIZE_ERROR(103, "上传文件大小不能超过10MB"),
    INVALID_ID(1000, "无效ID"),
    EXISTS(1001, "已存在"),
    NOT_EXISTS(1002, "不存在"),
    BAD_USERNAME_OR_PASSWORD(1003, "账户不存在或密码错误"),
    CNT_PASSWORD_ERR(1003, "密码错误次数过多，账户锁定！"),
    BAD_USERNAME_STATUS_INVALID(1004, "用户被禁用"),
    USERNAME_EXISTS(1005, "用户名已存在"),
    WEBSOCKET_SEND_FAIL(1006, "websocket 消息发送异常"),
    CLIENT_INVALID(1007, "无效的clientId"),
    CLIENT_BLOCKED(1008, "client认证已禁用"),
    INVALID_TOKEN(2001, "无效token"),
    INVALID_USER(2002, "无效用户"),
    INVALID_PERMISSION(2003, "抱歉，您目前无权执行此操作，请联系管理员获取相应权限。"),
    INVALID(10101, "无效的数据"),
    OTHER(1099, ""),
    UNKNOWN(10100, "服务异常"),
    DEBOUNCE(10102, "您的请求过于频繁，请稍后再试！"),

    ;

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;

    CommonResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 自定义断言，支持提供错误码和消息
     *
     * @param code    自定义错误码
     * @param message 自定义错误消息
     * @return 当前枚举常量
     */
    public CommonResponseEnum message(int code, String message) {
        this.setCode(code);
        this.setMessage(message);
        return this;
    }

    public CommonResponseEnum message(String message) {
        this.setMessage(message);
        return this;
    }

    private void setCode(int code) {
        this.code = code;
    }

    private void setMessage(String message) {
        this.message = message;
    }


    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
