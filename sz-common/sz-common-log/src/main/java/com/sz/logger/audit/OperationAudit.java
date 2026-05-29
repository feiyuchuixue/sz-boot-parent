package com.sz.logger.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 显式声明操作审计。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationAudit {

    /**
     * 操作名称。为空时使用 OpenAPI {@code @Operation(summary)}。
     */
    String name() default "";

    /**
     * 模块名称。为空时使用 OpenAPI {@code @Tag(name)}。
     */
    String module() default "";

    /**
     * 操作类型。默认根据 HTTP 方法自动推断。
     */
    OperationType operationType() default OperationType.AUTO;

    /**
     * 业务 ID SpEL，例如 {@code #id} 或 {@code #dto.id}。
     */
    String bizId() default "";

    /**
     * 当前接口是否记录请求参数。开启后仍会统一脱敏和截断。
     */
    boolean recordParams() default false;

    /**
     * 响应体记录策略。
     */
    BodyRecordMode responseBody() default BodyRecordMode.DEFAULT;
}
