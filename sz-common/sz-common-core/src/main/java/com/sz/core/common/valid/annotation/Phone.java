package com.sz.core.common.valid.annotation;

import com.sz.core.common.valid.validator.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手机号格式校验（中国大陆，1开头11位，第二位3-9）
 * <p>
 * 允许字段为 null 或空字符串（不做必填校验，必填请配合 @NotBlank 使用）
 * </p>
 *
 * @author sz
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {

    String message() default "手机号格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
