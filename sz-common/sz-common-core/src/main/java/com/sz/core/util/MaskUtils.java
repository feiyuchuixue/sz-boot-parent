package com.sz.core.util;

/**
 * 数据脱敏工具类
 *
 * @author sz-admin
 * @since 2025-07-01
 */
public class MaskUtils {

    private MaskUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 用户名脱敏：保留首尾字符，中间用 * 替代 例如：zhangsan -> z******n, ab -> a*b, a -> a
     *
     * @param username
     *            用户名
     * @return 脱敏后的用户名
     */
    public static String maskUsername(String username) {
        if (username == null || username.length() <= 1) {
            return username;
        }
        if (username.length() == 2) {
            return username.charAt(0) + "*";
        }
        int len = username.length();
        return username.charAt(0) + "*".repeat(len - 2) + username.charAt(len - 1);
    }

    /**
     * 邮箱脱敏：根据用户名长度智能脱敏，保留足够的识别信息
     * <p>
     * 脱敏规则（基于 @ 前用户名部分的长度）：
     * <ul>
     * <li>1-2 字符：保留第1个字符 + *，如 a@xx.com → a*@xx.com</li>
     * <li>3-4 字符：保留首尾各1个字符，如 test@xx.com → t**t@xx.com</li>
     * <li>5-6 字符：保留前2个 + 后1个字符，如 hello@xx.com → he**o@xx.com</li>
     * <li>7-10 字符：保留前3个 + 后2个字符，如 zhangsan@xx.com → zha***an@xx.com</li>
     * <li>11+ 字符：保留前3个 + 后3个字符，如 feiabcdfxue@163.com → fei****xue@163.com</li>
     * </ul>
     *
     * @param email
     *            邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 0) {
            return email;
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        int len = localPart.length();

        String maskedLocal;
        if (len <= 2) {
            // 1-2字符：保留第1个 + *
            maskedLocal = localPart.charAt(0) + "*";
        } else if (len <= 4) {
            // 3-4字符：保留首尾各1个
            maskedLocal = localPart.charAt(0) + "*".repeat(len - 2) + localPart.charAt(len - 1);
        } else if (len <= 6) {
            // 5-6字符：保留前2个 + 后1个
            maskedLocal = localPart.substring(0, 2) + "*".repeat(len - 3) + localPart.charAt(len - 1);
        } else if (len <= 10) {
            // 7-10字符：保留前3个 + 后2个
            maskedLocal = localPart.substring(0, 3) + "*".repeat(len - 5) + localPart.substring(len - 2);
        } else {
            // 11+字符：保留前3个 + 后3个
            maskedLocal = localPart.substring(0, 3) + "*".repeat(len - 6) + localPart.substring(len - 3);
        }

        return maskedLocal + domain;
    }

    /**
     * 手机号脱敏：保留前3位和后4位，中间用 **** 替代 例如：13812345678 -> 138****5678
     *
     * @param phone
     *            手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 身份证号脱敏：保留前6位和后4位，中间用 ******** 替代 例如：110101199001011234 -> 110101********1234
     *
     * @param idCard
     *            身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 10) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 银行卡号脱敏：保留前4位和后4位，中间用 **** **** **** 替代 例如：6222021234567890123 -> 6222 ****
     * **** 0123
     *
     * @param bankCard
     *            银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + " **** **** " + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 获取客户端 IP 地址 便捷方法，封装了异常处理
     *
     * @return 客户端 IP 地址，获取失败返回 "unknown"
     */
    public static String getClientIp() {
        try {
            return HttpReqResUtil.getIpAddress(HttpReqResUtil.getRequest());
        } catch (Exception e) {
            return "unknown";
        }
    }

}
