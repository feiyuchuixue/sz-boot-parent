package com.sz.core.common.enums;

import com.sz.core.common.exception.common.BusinessExceptionCustomAssert;

/**
 * 异常枚举类
 */
public enum CommonResponseEnum implements BusinessExceptionCustomAssert {

    // @formatter:off
    VALID_ERROR(100, "参数校验异常"),
    BAD_USERNAME_OR_PASSWORD(101, "账户不存在或密码错误"),
    CNT_PASSWORD_ERR(102, "密码错误次数过多，账户锁定！"),
    CLIENT_INVALID(103, "无效的ClientId"),
    CLIENT_BLOCKED(104, "Client认证已禁用"),
    INVALID_TOKEN(105, "无效Token"),
    INVALID_USER(106, "无效用户"),
    BAD_USERNAME_STATUS_INVALID(107, "用户被禁用"),
    INVALID_PERMISSION(108, "抱歉，您目前无权执行此操作，请联系管理员获取相应权限。"),
    WEBSOCKET_SEND_FAIL(109, "WebSocket消息发送异常"),
    DEBOUNCE(110, "您的请求过于频繁，请稍后再试！"),

    INVALID_ID(1000, "无效ID"),
    EXISTS(1001, "已存在"),
    NOT_EXISTS(1002, "不存在"),
    FILE_NOT_EXISTS(1003, "文件不存在"),
    FILE_UPLOAD_EXT_ERROR(1004, "上传文件类型错误"),
    FILE_UPLOAD_SIZE_ERROR(1005, "上传文件大小不能超过10MB"),
    FILE_UPLOAD_ERROR(1006, "上传文件失败"),
    USERNAME_EXISTS(1007, "用户名已存在"),
    INVALID(1008, "无效的数据"),
    EXCEL_IMPORT_ERROR(1009, "Excel导入失败"),
    CAPTCHA_LACK(1010, "验证参数缺失"),
    CAPTCHA_EXPIRED(1011, "验证码失效，请重新获取"),
    CAPTCHA_FAILED(1012, "验证失败"),
    CAPTCHA_LIMIT(1013,"验证码请求已达到最大次数"),
    BACKGROUND_NOT_EXISTS(1014, "背景图片不存在"),

    UNKNOWN(9999, "未知异常"),
    ;
    // @formatter:on

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
     * @param code
     *            自定义错误码
     * @param message
     *            自定义错误消息
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

    @Override
    public ErrorPrefixEnum getCodePrefixEnum() {
        return ErrorPrefixEnum.COMMON;
    }

}
