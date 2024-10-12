package com.sz.core.common.valid.annotation;

import com.sz.core.common.valid.validator.NotZeroValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotZeroValidator.class)
public @interface NotZero {

    String message() default "Value must be non-null and non-zero";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
