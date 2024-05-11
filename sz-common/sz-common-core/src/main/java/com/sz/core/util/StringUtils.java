package com.sz.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: sz
 * @date: 2021/9/7 8:02
 * @description: StringUtil
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private StringUtils() {
        throw new IllegalStateException("StringUtils class Illegal");
    }

    /**
     * @param s
     * @return java.lang.String
     * @describe: 首字母小写
     * @author sz
     * @date 2021-09-15 07:56:58
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * @param s
     * @return java.lang.String
     * @describe: 首字母大写
     * @author sz
     * @date 2021-09-15 07:57:16
     */
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * @param str             源字符串
     * @param pattern         正则rule
     * @param replaceArrValue 替换value,有序
     * @return java.lang.String
     * @describe: String正则替换
     * @author sz
     * @date 2021-11-25 14:19:50
     */
    public static String getRealKey(String str, String pattern, String[] replaceArrValue) {
        Matcher match = Pattern.compile(pattern).matcher(str);
        List<String> matchList = new ArrayList<>();
        while (match.find()) {
            matchList.add(match.group(1));
        }
        for (int i = 0; i < replaceArrValue.length; i++) {
            str = str.replace(matchList.get(i), replaceArrValue[i]);
        }
        return str;
    }

    public static String getRealKey(String sourceKey, String... replaceArrValue) {
        String pattern = "(\\$\\{\\w+\\})";
        return getRealKey(sourceKey, pattern, replaceArrValue);
    }

    public static String replacePlaceholders(String input, String... args) {
        // 编译一个正则表达式模式，用于匹配 ${...} 形式的占位符
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(input);

        // 使用StringBuilder来构建最终的字符串，它比StringBuffer更高效
        StringBuilder result = new StringBuilder();

        // 用于跟踪args参数的索引
        int index = 0;

        // 使用Matcher的find和appendReplacement方法来逐步替换占位符
        while (matcher.find()) {
            // 检查是否有足够的参数来替换占位符
            String replacement = (index < args.length) ? args[index] : matcher.group();
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            index++;
        }

        // 追加未替换部分的尾部到结果中
        matcher.appendTail(result);

        // 返回最终构建的字符串
        return result.toString();
    }

    public static String subWithLength(String str, int start, int length) {
        // 防止索引越界
        if (start < 0) {
            start = 0;
        }
        if (start >= str.length()) {
            return "";
        }

        // 计算截取的结束索引
        int end = Math.min(start + length, str.length());

        return str.substring(start, end);
    }


}
