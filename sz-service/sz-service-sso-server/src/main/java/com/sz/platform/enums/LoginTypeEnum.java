package com.sz.platform.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 登录方式
 * <p>
 * LoginTypeEnum
 *
 * @author sz
 * @since 2025/6/19
 */

@Getter
@RequiredArgsConstructor
public enum LoginTypeEnum {

    // @formatter:off
    // 用户名
    USERNAME,
    // 手机号
    PHONE,
    // 邮箱
    EMAIL
    ;
    // @formatter:on

}
