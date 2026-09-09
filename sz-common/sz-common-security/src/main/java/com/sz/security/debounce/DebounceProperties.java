package com.sz.security.debounce;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 接口防抖配置属性
 * <p>
 * 通过 sz.debounce.* 配置；由 DebounceAutoConfiguration 统一装配，无需单独 @Configuration。
 * </p>
 *
 * @author sz
 * @since 2024/9/18 17:19
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "sz.debounce")
public class DebounceProperties {

    /** 是否开启防抖，默认开启 */
    private boolean enabled = true;

    /** 全局默认锁定时间（ms），默认 1000ms */
    private long globalLockTime = 1000;

    /** 是否忽略 GET 请求，默认不忽略 */
    private boolean ignoreGetMethod;

}
