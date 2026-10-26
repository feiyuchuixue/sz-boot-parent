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

    /**
     * 是否启用 Controller 接口防抖。关闭时所有请求直接放行，即使类或方法标注 Debounce 也不会加锁；开启时 DebounceIgnore
     * 标注的 类或方法仍始终跳过防抖。命中重复请求时返回统一的防抖业务异常。
     */
    private boolean enabled = true;

    /**
     * 未标注 Debounce 时使用的 Redis 防抖锁时长，单位毫秒。Debounce.time() 会覆盖本值；本值直接作为 Redis 锁 TTL
     * 传入， 配置层不额外校验正数。
     *
     * @schemaExample 500 | 将未单独标注的接口锁定半秒
     */
    private long globalLockTime = 1000;

    /**
     * 是否跳过未标注 Debounce 的 GET 请求。true 只放行没有 Debounce 标注的 GET；类或方法显式标注 Debounce
     * 时仍执行防抖， DebounceIgnore 的优先级更高并直接放行。该字段不影响 POST、PUT、DELETE 等其他请求方法。
     */
    private boolean ignoreGetMethod;

}
