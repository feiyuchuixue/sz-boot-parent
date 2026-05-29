package com.sz.core.common.valid.validator;

import com.sz.core.common.valid.annotation.IdCard;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 身份证号校验器（中国大陆 18 位居民身份证）
 * <p>
 * 校验逻辑： 1. 格式正则：前6位地区码 + 8位出生日期 + 3位顺序码 + 1位校验码 2.
 * 出生日期合法（年1900-2099，月01-12，日01-31，简单范围校验） 3. ISO 7064 MOD 11-2 校验码算法 <br>
 * null 和空字符串视为合法（不做必填校验）
 * </p>
 *
 * @author sz
 */
public class IdCardValidator implements ConstraintValidator<IdCard, String> {

    /** 身份证正则：18位，最后一位可为X */
    private static final String ID_CARD_REGEX = "^\\d{17}[\\dX]$";

    /** ISO 7064 MOD 11-2 校验码对应表 */
    private static final char[] CHECK_CODE_TABLE = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /** 加权因子 */
    private static final int[] WEIGHT_FACTOR = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        // 基本格式校验
        String upper = value.toUpperCase();
        if (!upper.matches(ID_CARD_REGEX)) {
            return false;
        }
        // 出生日期合法性（简单范围校验）
        if (!isValidBirthDate(upper)) {
            return false;
        }
        // 校验码验证
        return isValidCheckCode(upper);
    }

    private boolean isValidBirthDate(String id) {
        String year = id.substring(6, 10);
        String month = id.substring(10, 12);
        String day = id.substring(12, 14);
        int y = Integer.parseInt(year);
        int m = Integer.parseInt(month);
        int d = Integer.parseInt(day);
        return y >= 1900 && y <= 2099 && m >= 1 && m <= 12 && d >= 1 && d <= 31;
    }

    private boolean isValidCheckCode(String id) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (id.charAt(i) - '0') * WEIGHT_FACTOR[i];
        }
        char expectedCheckCode = CHECK_CODE_TABLE[sum % 11];
        return id.charAt(17) == expectedCheckCode;
    }
}
