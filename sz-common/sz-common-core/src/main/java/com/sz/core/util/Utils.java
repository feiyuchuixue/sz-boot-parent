package com.sz.core.util;

import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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


}
