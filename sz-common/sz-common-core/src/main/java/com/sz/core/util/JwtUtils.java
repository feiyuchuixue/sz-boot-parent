package com.sz.core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sz.core.common.enums.UserSubjectEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: sz
 * @date: 2022/6/3 13:25
 * @description:
 */
@Deprecated
public class JwtUtils {
    /**
     * access_token 失效时间(ms) 24小时
     */
    public static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    /**
     * refresh_token 失效时间(ms) 7天
     */
    public static final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 项目类型
     */
    public static final String SUBJECT = "sub";

    /**
     * 用户id
     */
    public static final String USER_ID = "aud";

    /**
     * 失效时间
     */
    public static final String EXPIRE = "exp";

    /**
     * refreshToken
     */
    public static final String REFRESH_TOKEN = "refreshToken";

    /**
     * 加密盐值
     */
    private static final String SECRET = "J0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";

    private static final String REFRESH_SECRET = "J0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ7";

    /**
     * 创建accessToken
     *
     * @param subjectEnum
     * @param username
     * @return
     */
    public static String createAccessToken(UserSubjectEnum subjectEnum, String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION);
        Map<String, Object> payloadMap = new HashMap<>(1);
        payloadMap.put("username", username);
        return JWT.create()
                .withPayload(payloadMap)
                .withSubject(subjectEnum.name())
                .withExpiresAt(expiration)
                .sign(algorithm);
    }


    public static String createAccessToken(UserSubjectEnum subjectEnum, String username, Date expiration) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        Map<String, Object> payloadMap = new HashMap<>(1);
        payloadMap.put("username", username);
        return JWT.create()
                .withPayload(payloadMap)
                .withSubject(subjectEnum.name())
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    /**
     * 创建refreshToken
     *
     * @param subjectEnum
     * @param username
     * @return
     */
    public static String createRefreshToken(UserSubjectEnum subjectEnum, String username) {
        Algorithm algorithm = Algorithm.HMAC256(REFRESH_SECRET);
        Date expiration = new Date(System.currentTimeMillis() + REFRESH_EXPIRATION);
        Map<String, Object> payloadMap = new HashMap<>(1);
        payloadMap.put("username", username);
        return JWT.create()
                .withPayload(payloadMap)
                .withSubject(subjectEnum.name())
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    public static String createRefreshToken(UserSubjectEnum subjectEnum, String username, Date expiration) {
        Algorithm algorithm = Algorithm.HMAC256(REFRESH_SECRET);
        Map<String, Object> payloadMap = new HashMap<>(1);
        payloadMap.put("username", username);
        return JWT.create()
                .withPayload(payloadMap)
                .withSubject(subjectEnum.name())
                .withExpiresAt(expiration)
                .sign(algorithm);
    }


    public static boolean verifyAccessToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean verifyRefreshToken(String token) throws Exception {
        JWT.require(Algorithm.HMAC256(REFRESH_SECRET)).build().verify(token);
        return true;
    }

    public static DecodedJWT getInfoFromJwtToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }

    public static DecodedJWT getRefreshInfoFromJwtToken(String token) {
        return JWT.require(Algorithm.HMAC256(REFRESH_SECRET)).build().verify(token);
    }

    public static void main(String[] args) {
/*        String jwtToken = createAccessToken(UserSubjectEnum.SYSTEM, "1");
        System.out.println("jwtToken ==" + jwtToken);

        DecodedJWT decodedJWT = getInfoFromJwtToken(jwtToken);

        System.out.println("jwtToken 解密1 ==" + decodedJWT.getToken());
        System.out.println("jwtToken 解密2 ==" + decodedJWT.getHeader());
        System.out.println("jwtToken 解密3 ==" + decodedJWT.getSignature());
        System.out.println("jwtToken 解密4 ==" + decodedJWT.getPayload());
        System.out.println(" 5== " + decodedJWT.getClaims());
        System.out.println(" 6== " + decodedJWT.getSubject());
        System.out.println(" 7== " + decodedJWT.getAudience());
        System.out.println(" 8== " + decodedJWT.getExpiresAt().getTime());
        System.out.println(" 9== " + System.currentTimeMillis());*/
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION);

        // 创建 SimpleDateFormat 实例，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 使用 SimpleDateFormat 将 Date 对象转换为字符串
        String dateString = sdf.format(expiration);

        // 打印结果
        System.out.println("Date as String: " + dateString);
    }
}
