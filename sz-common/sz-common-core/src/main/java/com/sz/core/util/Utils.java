package com.sz.core.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author: sz
 * @date: 2022/8/23 10:20
 * @description:
 */
public class Utils {

    private static final String UNKNOWN = "null";

    private Utils() {
        throw new IllegalStateException("Utils class Illegal");
    }

    public static String generateUUIDs() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Boolean isNotNull(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static Boolean isNotNull(Integer str) {
        return str != null;
    }

    public static Boolean isNotNull(Boolean str) {
        return str != null;
    }

    public static Boolean isNotNull(Double str) {
        return str != null;
    }

    public static Boolean isNotNull(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    public static Boolean isNotNull(Object obj) {
        return obj != null && !"".equals(obj) && !"".equals(obj.toString().trim());
    }

    public static Boolean isNotNull(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static Boolean isNotNull(List<?> list) {
        return list != null && list.size() > 0;
    }

    public static Integer getIntVal(Object obj) {
        return (isNotNull(obj) && !(UNKNOWN).equals(obj)) ? (Integer.valueOf(obj.toString())) : 0;
    }

    public static Long getLongVal(Object obj) {
        return (isNotNull(obj) && !(UNKNOWN).equals(obj)) ? (Long.valueOf(obj.toString())) : 0L;
    }

    public static String getStringVal(Object obj) {
        if (isNotNull(obj) && !(UNKNOWN).equals(obj)) {
            return obj.toString();
        } else {
            return "";
        }
    }

    public static String getTraceId() {
        return generateUUIDs();
    }

    public static String md5(String str) {
        String md5 = "  ";
        try {
            md5 = DigestUtils.md5DigestAsHex(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            new Exception(e.getMessage());
        }
        return md5;
    }

    public static Field[] getFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                fieldList.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fieldList.toArray(new Field[0]);
    }

    public static Object invokeGetter(Object obj, String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method getter = obj.getClass().getMethod(getterName);
        return getter.invoke(obj);
    }

    /**
     * 生成用于接口防抖的RequestId
     * 
     * @param request
     *            HttpServletRequest对象
     * @return 接口防抖用的RequestId
     */
    public static String generateDebounceRequestId(HttpServletRequest request) {
        String ip = HttpReqResUtil.getIpAddress(request);
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String queryString = request.getQueryString();
        return ip + ":" + method + ":" + uri + ":" + queryString + ":" + userAgent;
    }

    /**
     * 生成用于用户验证码的RequestId
     * 
     * @param request
     *            HttpServletRequest对象
     * @return 用户验证码用的RequestId
     */
    public static String generateCaptchaRequestId(HttpServletRequest request) {
        String ip = HttpReqResUtil.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        return ip + ":" + userAgent;
    }

    public static String generateSha256Id(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
