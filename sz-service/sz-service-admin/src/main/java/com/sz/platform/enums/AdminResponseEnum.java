package com.sz.platform.enums;

import com.sz.core.common.exception.common.BusinessExceptionCustomAssert;

/**
 * 异常枚举类 适用于Admin模块的
 */
public enum AdminResponseEnum implements BusinessExceptionCustomAssert {

    NO_HEADER_TOKEN(2003, "无效的token"),
    INVALID_USER(2004, "无效用户"),
    INVALID_ID(2005, "无效的id"),
    SYS_DICT_TYPE_EXISTS(2006, "字典类型已存在"),
    SYS_DICT_EXISTS(2007, "字典已存在"),

    SYS_MENU_NOT_EXISTS(2008, "菜单不存在"),

    SYS_ROLE_EXISTS(2009, "角色已存在"),
    MENU_PATH_EXISTS(2010, "路由地址已存在"),

    SYS_UPLOAD_FILE_ERROR(2011, "上传文件失败"),

    MENU_NAME_EXISTS(2012, "路由名称已存在"),

    SYS_CONFIG_EXISTS(2013, "参数key已存在"),
    INVALID_EXISTS(2014, "已存在");

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
     * @param code    自定义错误码
     * @param message 自定义错误消息
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
        return this.message;
    }
}
