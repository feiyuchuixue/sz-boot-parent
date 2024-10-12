package com.sz.core.common.annotation;

import java.lang.annotation.*;

/**
 * @author: sz
 * @date: 2022/8/29 10:43
 * @description: 自定义注解 - 用于get请求对象传递字段的Snake to Camel 转换
 *               <p>
 *               作用与method param 属性上，同 @RequestParam
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
public @interface QueryStringArgs {

    boolean required() default true;

}
