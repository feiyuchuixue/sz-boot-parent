package com.sz.core.common.annotation;

import java.lang.annotation.*;

/**
 * 忽略防抖注解
 *
 * @ClassName DebounceIgnore
 * @Author sz
 * @Date 2024/9/18 11:20
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface DebounceIgnore {

    boolean required() default true;

}
