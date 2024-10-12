package com.sz.logger.utils;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName DesensitizationUtil
 * @Description 脱敏工具类
 * @Author 柳成荫
 * @Date 2021/1/9
 */
public class DesensitizationUtil {

    /**
     * 正则匹配模式 -
     * 该正则表达式第三个()可能无法匹配以某些特殊符号开头和结尾的（如果像密码这种字段，前后如果有很多特殊字段，则无法匹配，建议密码直接加密，无需脱敏）
     */
    public static final Pattern REGEX_PATTERN = Pattern
            .compile("\\s*([\"]?[\\w]+[\"]?)(\\s*[:=]+[^\\u4e00-\\u9fa5@,.*{\\[\\w]*\\s*)([\\u4e00-\\u9fa5_\\-@.\\w]+)[\\W&&[^\\-@.]]?\\s*");
    // 该正则表达式第三个()可以匹配以某些特殊字符开头和结尾的，但是对于日志来说，处理也很麻烦
    // public static final Pattern REGEX_PATTERN =
    // Pattern.compile("\\s*([\"]?[\\w]+[\"]?)(\\s*[:：=><]+\\s*)([\\S]+[\\u4e00-\\u9fa5\\w]+[\\S]+)[\\W&&[^\\-@.]]?\\s*");

    /**
     * 匹配非数字
     */
    public static final Pattern REGEX_NUM = Pattern.compile("[^0-9]");

    /**
     * 是否开启脱敏
     */
    public static Boolean openFlag = false;

    /**
     * 是否忽略key的大小写
     */
    public static Boolean ignoreFlag = true;

    /**
     * 作为ignoreFlag初始的标记
     */
    private static Boolean initIgnoreFlag = false;

    /**
     * 作为openFlag初始化的标记
     */
    private static Boolean initOpenFlag = false;

    /**
     * 所有key:value配置匹配对
     */
    public static Map<String, Object> allPattern;

    /**
     * key为全小写的allPattern - pattern和patterns
     */
    public static Map<String, Object> lowerCaseAllPattern;

    /**
     * 手机
     */
    public static final String PHONE = "phone";

    /**
     * 邮箱
     */
    public static final String EMAIL = "email";

    /**
     * 身份证
     */
    public static final String IDENTITY = "identity";

    /**
     * 自定义
     */
    public static final String OTHER = "other";

    /**
     * 密码
     */
    public static final String PASSWORD = "password";

    public static final String PWD = "pwd";

    /**
     * 将event对象的formattedMessage脱敏
     *
     * @param eventFormattedMessage
     *            LoggingEvent的formattedMessage属性
     * @return 脱敏后的日志信息
     */
    public String customChange(String eventFormattedMessage) {
        try {
            // 原始信息 - 格式化后的
            String originalMessage = eventFormattedMessage;
            boolean flag = false;
            // 获取Yml配置文件内容 - Map格式
            Map<String, Object> patternMap = YmlUtils.patternMap;
            if (!CollectionUtils.isEmpty(patternMap)) {
                // 如果没有开启脱敏，返回""，则不会做脱敏操作
                if (!this.checkOpen(patternMap)) {
                    return "";
                }
                // 获取一个原始Message的正则匹配器
                Matcher regexMatcher = REGEX_PATTERN.matcher(eventFormattedMessage);
                // 如果部分匹配（一个对象/JSON字符串/Map/List<对象/Map>等会有多个匹配），就根据分组来获取key和value
                while (regexMatcher.find()) {
                    // group(1)就是key，group(2)就是分隔符(如：和=),group(3)就是value
                    try {
                        // 获取key - 将引号替换掉，去掉两边空格（JSON字符串去引号）
                        String key = regexMatcher.group(1).replaceAll("\"", "").trim();
                        // 获取原始Value
                        String originalValue = regexMatcher.group(3);
                        // 获取Key对应规则
                        Object keyPatternValue = this.getKeyIgnoreCase(key);
                        if (null != keyPatternValue && null != originalValue && !"null".equals(originalValue)) {
                            // 将原始Value - 引号替换掉，去掉两边空格（JSON字符串去引号）
                            String value = originalValue.replaceAll("\"", "").trim();
                            if (!"null".equals(value) || value.equalsIgnoreCase(key)) {
                                String patternVales = getMultiplePattern(keyPatternValue, value);
                                if ("".equals(patternVales)) {
                                    // 不符规则/没有规则的不能影响其他符合规则的
                                    continue;
                                }
                                patternVales = patternVales.replaceAll(" ", "");
                                if (PASSWORD.equalsIgnoreCase(patternVales)) {
                                    String origin = regexMatcher.group(1) + regexMatcher.group(2) + regexMatcher.group(3);
                                    originalMessage = originalMessage.replace(origin, regexMatcher.group(1) + regexMatcher.group(2) + "******");
                                    flag = true;
                                    // 密码级别的，直接替换为全*，继续下一轮匹配
                                    continue;
                                }
                                // 原始的规则（完整）
                                String originalPatternValues = patternVales;
                                // 判断这个规则是否带括号，带括号的需要把括号拿出来 - 核心规则
                                String filterData = this.getBracketPattern(patternVales);
                                if (!"".equals(filterData)) {
                                    patternVales = filterData;
                                }
                                // 以逗号分割
                                String[] split = patternVales.split(",");
                                value = getReplaceValue(value, patternVales, split, originalPatternValues);
                                if (value != null && !"".equals(value)) {
                                    flag = true;
                                    String origin = regexMatcher.group(1) + regexMatcher.group(2) + regexMatcher.group(3);
                                    originalMessage = originalMessage.replace(origin, regexMatcher.group(1) + regexMatcher.group(2) + value);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 捕获到异常，直接返回结果(空字符串) - 这个异常可能发生的场景：同时开启控制台和输出文件的时候
                        // 当控制台进行一次脱敏之后，文件的再去脱敏，是对脱敏后的message脱敏，则正则匹配会出现错误
                        // 比如123456789@.com 脱敏后：123***456789@qq.com，正则匹配到123，这个123去substring的时候会出错
                        return "";
                    }
                }
            }
            return flag ? originalMessage : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取替换后的value
     * 
     * @param value
     *            value
     * @param patternVales
     *            核心规则
     * @param split
     *            分割
     * @param originalPatternValues
     *            原始规则
     * @return
     */
    private String getReplaceValue(String value, String patternVales, String[] split, String originalPatternValues) {
        if (split.length >= 2 && !"".equals(patternVales)) {
            String append = "";
            String start = REGEX_NUM.matcher(split[0]).replaceAll("");
            String end = REGEX_NUM.matcher(split[1]).replaceAll("");
            int startSub = Integer.parseInt(start) - 1;
            int endSub = Integer.parseInt(end) - 1;
            // 脱敏起点/结尾符下标
            int index;
            String flagSub;
            int indexOf;
            int newValueL;
            String newValue;
            // 脱敏结尾
            if (originalPatternValues.contains(">")) {
                // 获取>的下标
                index = originalPatternValues.indexOf(">");
                // 获取标志符号
                flagSub = originalPatternValues.substring(0, index);
                // 获取标志符号的下标
                indexOf = value.indexOf(flagSub);
                // 获取标志符号前面数据
                newValue = value.substring(0, indexOf);
                // 获取数据的长度
                newValueL = newValue.length();
                // 获取标识符及后面的数据
                append = value.substring(indexOf);
                value = this.dataDesensitization(Math.max(startSub, 0), endSub >= 0 ? (endSub <= newValueL ? endSub : newValueL - 1) : 0, newValue) + append;
            } else if (originalPatternValues.contains("<")) {
                // 脱敏起点
                index = originalPatternValues.indexOf("<");
                flagSub = originalPatternValues.substring(0, index);
                indexOf = value.indexOf(flagSub);
                newValue = value.substring(indexOf + 1);
                newValueL = newValue.length();
                append = value.substring(0, indexOf + 1);
                value = append + this.dataDesensitization(Math.max(startSub, 0), endSub >= 0 ? (endSub <= newValueL ? endSub : newValueL - 1) : 0, newValue);
            } else if (originalPatternValues.contains(",")) {
                newValueL = value.length();
                value = this.dataDesensitization(Math.max(startSub, 0), endSub >= 0 ? (endSub <= newValueL ? endSub : newValueL - 1) : 0, value);
            }
        } else if (!"".equals(patternVales)) {
            int beforeIndexOf = patternVales.indexOf("*");
            int last = patternVales.length() - patternVales.lastIndexOf("*");
            int lastIndexOf = value.length() - last;
            value = this.dataDesensitization(beforeIndexOf, lastIndexOf, value);
        }
        return value;
    }

    /**
     * 根据key获取对应的规则(也许是Map，也许是String)
     *
     * @param key
     *            key
     * @return key对应的规则(也许是Map ， 也许是String)
     */
    private Object getKeyIgnoreCase(String key) {
        // 获取所有pattern
        if (CollectionUtils.isEmpty(allPattern)) {
            allPattern = YmlUtils.getAllPattern();
        }
        // 作为ignoreFlag初始化的标记，第一次ignoreFlag需要从Yml中获取是否开启
        // 后面就不用去Yml里获取了
        if (!initIgnoreFlag) {
            initIgnoreFlag = true;
            // 仅在第一次会去获取，无论true还是false(默认是开启忽略大小写)
            ignoreFlag = YmlUtils.getIgnore();
            if (ignoreFlag) {
                // 如果忽略大小写，就去获取一份key小写化的allPattern
                lowerCaseAllPattern = this.transformUpperCase(allPattern);
            }
        }
        // 只有忽略大小写的时候，才去从lowerCaseAllPattern里获取
        if (ignoreFlag) {
            return lowerCaseAllPattern.get(key.toLowerCase());
        } else {
            // 否则从原始的pattern中取
            return allPattern.get(key);
        }
    }

    /**
     * 将pattern的key值全部转换为小写
     *
     * @param pattern
     *            pattern
     * @return 转换后的pattern
     */
    public Map<String, Object> transformUpperCase(Map<String, Object> pattern) {
        Map<String, Object> resultMap = new HashMap();
        if (pattern != null && !pattern.isEmpty()) {
            // 获取Key的Set集合
            Set<String> keySet = pattern.keySet();
            Iterator<String> iterator = keySet.iterator();
            // 黄线强迫症，用for代替while
            for (; iterator.hasNext();) {
                String key = iterator.next();
                // 把key转换为小写字符串
                String newKey = key.toLowerCase();
                // 重新放入
                resultMap.put(newKey, pattern.get(key));
            }
        }
        return resultMap;
    }

    /**
     * 获取规则字符串
     *
     * @param patternVale
     *            规则
     * @param newValue
     *            key对应的值 - 如 name:liuchengyin 这个参数就是liuchengyn
     * @return 规则的字符串
     */
    private String getMultiplePattern(Object patternVale, String newValue) {
        if (patternVale instanceof String) {
            // 如果规则是String类型，直接转换为String类型返回
            return (String) patternVale;
        } else if (patternVale instanceof Map) {
            // 获取规则 - Map类型（不推荐，有风险）
            return this.getPatternByMap((Map<String, Object>) patternVale, newValue);
        } else { // 获取规则 - List<Map>类型,一个Key可能有多种匹配规则
            if (patternVale instanceof List) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) patternVale;
                if (!CollectionUtils.isEmpty(list)) {
                    Iterator<Map<String, Object>> iterator = list.iterator();
                    // 遍历每一种规则
                    for (; iterator.hasNext();) {
                        Map<String, Object> map = iterator.next();
                        String patternValue = this.getPatternByMap(map, newValue);
                        // 如果是空的，表示没匹配上该规则，去匹配下一个规则
                        if (!"".equals(patternValue)) {
                            return patternValue;
                        }
                    }
                }
            }
            return "";
        }
    }

    /**
     * 获取规则
     *
     * @param map
     *            规则
     * @param value
     *            key对应的值 - 如 name:liuchengyin 这个参数就是liuchengyn
     * @return
     */
    private String getPatternByMap(Map<String, Object> map, String value) {
        if (CollectionUtils.isEmpty(map)) {
            // 为空就是无规则
            return "";
        } else {
            // 获取匹配规则 - 自定义规则（正则）
            Object customRegexObj = map.get("customRegex");
            // 获取脱敏方式
            Object positionObj = map.get("position");
            // 获取匹配规则 - 自定义规则（正则）
            String customRegex = "";
            // position必须有
            String position = "";
            if (customRegexObj instanceof String) {
                customRegex = (String) customRegexObj;
            }
            if (positionObj instanceof String) {
                position = (String) positionObj;
            }
            // 如果日志中的值能够匹配，直接返回其对应的规则
            if (!"".equals(customRegex) && value.matches(customRegex)) {
                return position;
            } else {
                // 如果不能匹配到正则，就看他是不是内置规则
                Object defaultRegexObj = map.get("defaultRegex");
                String defaultRegex = "";
                if (defaultRegexObj instanceof String) {
                    defaultRegex = (String) defaultRegexObj;
                }
                // 这段代码写的多多少少感觉有点问题，可以写在一个if里，但是阿里检测代码的工具会警告
                if (!"".equals(defaultRegex)) {
                    if (IDENTITY.equals(defaultRegex) && isIdentity(value)) {
                        return position;
                    } else if (EMAIL.equals(defaultRegex) && isEmail(value)) {
                        return position;
                    } else if (PHONE.equals(defaultRegex) && isMobile(value)) {
                        return position;
                    } else if (OTHER.equals(defaultRegex)) {
                        return position;
                    }
                }
                return "";
            }
        }
    }

    /**
     * 获取规则 - 判断是否带括号，带括号则返回括号内数据
     * 
     * @param patternVales
     *            规则
     * @return 规则
     */
    private String getBracketPattern(String patternVales) {
        // 是否存在括号
        if (patternVales.contains("(")) {
            int startCons = patternVales.indexOf("(");
            int endCons = patternVales.indexOf(")");
            patternVales = patternVales.substring(startCons + 1, endCons);
            return patternVales;
        } else {
            return "";
        }
    }

    public static boolean isEmail(String str) {
        return str.matches("^[\\w-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    public static boolean isIdentity(String str) {
        return str.matches("(^\\d{18}$)|(^\\d{15}$)");
    }

    public static boolean isMobile(String str) {
        return str.matches("^1[0-9]{10}$");
    }

    /**
     * 检查是否开启脱敏
     *
     * @param pattern
     *            Yml配置文件内容 - Map格式
     * @return 是否开启脱敏
     */
    private Boolean checkOpen(Map<String, Object> pattern) {
        // 作为openFlag初始化的标记，第一次openFlag需要从Yml中获取是否开启
        // 后面就不用去Yml里获取了
        if (!initOpenFlag) {
            initOpenFlag = true;
            // 仅在第一次会去获取
            openFlag = YmlUtils.getOpen();
        }
        // 第二次以后openFlag已经有值，无论true还是false(默认是未开启)
        return openFlag;
    }

    /**
     * 脱敏处理
     * 
     * @param start
     *            脱敏开始下标
     * @param end
     *            脱敏结束下标
     * @param value
     *            value
     * @return
     */
    public String dataDesensitization(int start, int end, String value) {
        char[] chars;
        int i;
        // 正常情况 - end在数组长度内
        if (start >= 0 && end + 1 <= value.length()) {
            chars = value.toCharArray();
            // 脱敏替换
            for (i = start; i < chars.length && i < end + 1; ++i) {
                chars[i] = '*';
            }
            return new String(chars);
        } else if (start >= 0 && end >= value.length()) {
            // 非正常情况 - end在数组长度外
            chars = value.toCharArray();
            for (i = start; i < chars.length; ++i) {
                chars[i] = '*';
            }
            return new String(chars);
        } else {
            // 不符要求，不脱敏
            return value;
        }
    }
}