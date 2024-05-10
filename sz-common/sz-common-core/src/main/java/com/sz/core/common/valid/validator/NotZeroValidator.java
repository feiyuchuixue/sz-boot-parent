package com.sz.core.common.valid.validator;

import com.sz.core.common.valid.annotation.NotZero;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotZeroValidator implements ConstraintValidator<NotZero, Number> {

    @Override
    public void initialize(NotZero constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }

        if (value instanceof Integer) {
            return value.intValue() != 0;
        }

        if (value instanceof Long) {
            return value.longValue() != 0L;
        }
        return true;
    }
}
