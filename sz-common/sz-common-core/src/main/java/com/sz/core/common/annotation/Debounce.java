package com.sz.core.common.annotation;

import java.lang.annotation.*;

/**
 * 防抖注解（自定义防抖时间）
 *
 * @ClassName DebounceIgnore
 * @Author sz
 * @Date 2024/9/18 11:20
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Debounce {

    /**
     * 默认防抖时间，1000 ms
     *
     * @return
     */
    long time() default 1000;

}
