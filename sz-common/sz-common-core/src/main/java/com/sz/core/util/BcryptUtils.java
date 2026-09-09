package com.sz.core.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BcryptUtils {

    /**
     * BCrypt cost（推荐 10～12）
     */
    private static final int COST = 12;

    /**
     * 单例 Encoder BCryptPasswordEncoder 是线程安全的，可以复用
     */
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(COST);

    /**
     * 私有构造，禁止实例化
     */
    private BcryptUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 密码加密
     */
    public static String hashPwd(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("rawPassword must not be null or empty");
        }
        return ENCODER.encode(rawPassword);
    }

    /**
     * 密码校验
     */
    public static boolean matchEncoderPwd(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, encodedPassword);
    }

}
