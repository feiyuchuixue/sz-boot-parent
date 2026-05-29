package com.sz.platform.enums;

import com.sz.core.common.enums.ErrorPrefixEnum;
import com.sz.core.common.enums.ResponseEnumTemplate;

/**
 * 异常枚举类 适用于Admin模块的
 */
public enum AdminResponseEnum implements ResponseEnumTemplate<AdminResponseEnum> {

    // @formatter:off
    MENU_NAME_EXISTS(1001, "Menu路由名称已存在"),
    ;
    // @formatter:on

    /**
     * 返回码
     */
    private final int code;

    /**
     * 返回消息
     */
    private final String message;

    AdminResponseEnum(int code, String message) {
        this.code = code;
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