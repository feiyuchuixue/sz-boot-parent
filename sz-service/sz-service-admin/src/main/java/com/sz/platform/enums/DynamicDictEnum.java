package com.sz.platform.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 动态字典枚举类
 *
 * @ClassName DynamicDictEnum
 * @Author sz
 * @Date 2024/8/22 10:11
 * @Version 1.0
 */

@Getter
@RequiredArgsConstructor
public enum DynamicDictEnum {

    // @formatter:off
    DYNAMIC_USER_OPTIONS("dynamic_user_options", "用户信息");
    // @formatter:on
    private final String typeCode; // 类型代码

    private final String name; // 名称

}
