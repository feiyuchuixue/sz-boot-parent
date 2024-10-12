package com.sz.platform.enums;

import com.sz.core.common.enums.ErrorPrefixEnum;
import com.sz.core.common.exception.common.BusinessExceptionCustomAssert;

/**
 * 异常枚举类 适用于Admin模块的
 */
public enum AdminResponseEnum implements BusinessExceptionCustomAssert {

    // @formatter:off
    MENU_NAME_EXISTS(1001, "Menu路由名称已存在"),
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

    AdminResponseEnum(int code, String message) {
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
    public AdminResponseEnum message(int code, String message) {
        this.setCode(code);
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
        return message;
    }

    @Override
    public ErrorPrefixEnum getCodePrefixEnum() {
        return ErrorPrefixEnum.ADMIN;
    }

}
