package com.sz.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicDeleteFill {

    /**
     * 删除时间字段名（数据库列名），空字符串表示不填充 默认值 "delete_time"
     */
    String deleteTimeColumn() default "delete_time";

    /**
     * 删除人字段名（数据库列名），空字符串表示不填充 默认值 "delete_id"
     */
    String deleteByColumn() default "delete_id";

}
