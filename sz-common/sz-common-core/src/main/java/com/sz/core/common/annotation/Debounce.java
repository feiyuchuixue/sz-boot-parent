package com.sz.core.common.annotation;

import java.lang.annotation.*;

/**
 * 防抖注解（自定义防抖时间）
 * <p>
 * 自定义防抖注解
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2024/9/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Debounce {

    /**
     * 默认防抖时间，1000 ms
     *
     * @return 超时时间（ms）
     */
    long time() default 1000;

}
