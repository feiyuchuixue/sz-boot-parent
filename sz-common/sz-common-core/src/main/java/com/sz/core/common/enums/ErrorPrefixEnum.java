package com.sz.core.common.enums;

import lombok.Getter;

/**
 * @ClassName ErrorPrefixEnum
 * @Author sz
 * @Date 2024/10/11 14:06
 * @Version 1.0
 */
@Getter
public enum ErrorPrefixEnum {

    // @formatter:off
    COMMON("C", "common异常"),
    ADMIN("A", "admin异常"),

    // ...其他业务模块的异常前缀
    ;
    // @formatter:on

    /**
     * 前缀标识
     */
    private final String prefix;

    /**
     * 描述
     */
    private final String description;

    ErrorPrefixEnum(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }
}
