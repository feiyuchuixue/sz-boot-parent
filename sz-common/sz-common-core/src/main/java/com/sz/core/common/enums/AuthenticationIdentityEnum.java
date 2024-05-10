package com.sz.core.common.enums;


import lombok.Getter;

/**
 * 认证身份标识枚举
 *
 * @author sz
 * @date 2021/10/4
 */
public enum AuthenticationIdentityEnum {

    USERNAME("username", "用户名"),
    MOBILE("mobile", "手机号"),
    OPENID("openId", "开放式认证系统唯一身份标识");

    @Getter
    private String value;

    @Getter
    private String label;

    AuthenticationIdentityEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static AuthenticationIdentityEnum find(String typeCode) {
        for (AuthenticationIdentityEnum value : AuthenticationIdentityEnum.values()) {
            if (typeCode.equals(value.getLabel())) {
                return value;
            }
        }
        return null;
    }


}
