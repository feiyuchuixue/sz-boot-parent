package com.sz.core.common.valid.validator;

import com.sz.core.common.valid.annotation.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 手机号校验器
 * <p>
 * 规则：1开头，第二位3-9，共11位数字（中国大陆手机号） null 和空字符串视为合法（不做必填校验）
 * </p>
 *
 * @author sz
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.matches(PHONE_REGEX);
    }
}
