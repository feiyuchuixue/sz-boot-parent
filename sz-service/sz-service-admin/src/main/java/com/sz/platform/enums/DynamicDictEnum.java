package com.sz.platform.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 动态字典枚举类（可选工具类，非强依赖）
 *
 * DynamicDictEnum
 *
 * @author sz
 * @since 2024/8/22 10:11
 * @version 1.0
 */

@Getter
@RequiredArgsConstructor
public enum DynamicDictEnum {

    // @formatter:off
    DYNAMIC_USER_OPTIONS("user_options", "用户信息"),
    DYNAMIC_DEPT_OPTIONS("dept_options", "部门信息")
    ;
    // @formatter:on
    private final String typeCode; // 类型代码

    private final String name; // 名称

}
