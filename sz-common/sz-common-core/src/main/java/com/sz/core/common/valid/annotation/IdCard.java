package com.sz.core.common.valid.annotation;

import com.sz.core.common.valid.validator.IdCardValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 身份证号格式校验（中国大陆 18 位居民身份证）
 * <p>
 * 校验规则：
 * 1. 格式：17位数字 + 1位校验位（数字或X）
 * 2. 出生日期合法性（年月日范围）
 * 3. 最后一位校验码（ISO 7064 MOD 11-2）
 * <br>
 * 允许字段为 null 或空字符串（不做必填校验，必填请配合 @NotBlank 使用）
 * </p>
 *
 * @author sz
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdCardValidator.class)
public @interface IdCard {

    String message() default "身份证号格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
