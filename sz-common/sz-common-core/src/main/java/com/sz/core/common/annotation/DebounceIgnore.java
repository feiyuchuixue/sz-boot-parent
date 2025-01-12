package com.sz.core.common.annotation;

import java.lang.annotation.*;

/**
 * 忽略防抖注解
 *
 * @author sz
 * @version 1.0
 * @since 2024/9/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface DebounceIgnore {

    boolean required() default true;

}
