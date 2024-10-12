package com.sz.logger.utils;

import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * @ClassName YmlUtils
 * @Description Yml配置文件操作相关
 * @Author 柳成荫
 * @Date 2021/1/9
 */
public class YmlUtils {

    /** 默认脱敏配置文件名 - 默认在resources目录下 */
    public static String PROPERTY_NAME = "logback-desensitize.yml";

    /** Key：pattern - 单规则 */
    public static final String PATTERN = "pattern";

    /** Key：patterns - 多规则 */
    public static final String PATTERNS = "patterns";

    /** Key:open - 是否开启脱敏 */
    public static final String OPEN_FLAG = "open";

    /** Key:ignore - 是否开启忽略大小写匹配 */
    public static final String IGNORE = "ignore";

    /** Key:脱敏配置文件头Key */
    public static final String YML_HEAD_KEY = "log-desensitize";

    /** key:patterns对应key下的规则Key */
    public static final String CUSTOM = "custom";

    /** Yml脱敏配置文件内容 - Map格式 */
    public static Map<String, Object> patternMap;

    public static final DumperOptions OPTIONS = new DumperOptions();

    static {
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        patternMap = getYmlByName(PROPERTY_NAME);
    }

    /**
     * 获取Yml配置文件的内容 - 以Map的格式
     * 
     * @param fileName
     *            Yml配置文件名
     * @return 配置信息（Map格式）
     */
    private static Map<String, Object> getYmlByName(String fileName) {
        if (CollectionUtils.isEmpty(patternMap)) {
            Object fromYml = null;
            try {
                // 获取Yml配置文件的Map对象
                fromYml = getFromYml(fileName, YML_HEAD_KEY);
                // LinkedHashMap，如果不是Map类型（比如配置文件里只有log-desensitize=123456），直接返回patternMap本身
                if (fromYml instanceof Map) {
                    return (Map) fromYml;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return patternMap;
    }

    /**
     * 通过key获取value从yml配置文件
     * 
     * @param fileName
     *            Yml文件名
     * @param key
     *            key
     * @return value或者map本身
     */
    public static Object getFromYml(String fileName, String key) {
        // 创建一个Yaml对象
        Yaml yaml = new Yaml(OPTIONS);
        // 获得流
        InputStream inputStream = YmlUtils.class.getClassLoader().getResourceAsStream(fileName);
        HashMap<String, Object> map = (HashMap<String, Object>) yaml.loadAs(inputStream, HashMap.class);
        // 如果map内有值，直接返回key对应的Value，否则返回map本身
        return Objects.nonNull(map) && map.size() > 0 ? map.get(key) : map;
    }

    /**
     * 获取key为pattern的值
     * 
     * @return pattern对应的map，或者null（如pattern=123这种情况）
     */
    public static Map<String, Object> getPattern() {
        Object pattern = patternMap.get(PATTERN);
        if (pattern instanceof Map) {
            return (Map<String, Object>) pattern;
        } else {
            return null;
        }
    }

    /**
     * 获取所有pattern，含key为pattern，key为patterns
     * 
     * @return pattern
     */
    public static Map<String, Object> getAllPattern() {
        Map<String, Object> allPattern = new HashMap<String, Object>();
        Map<String, Object> pattern = getPattern();
        Map<String, Object> patterns = getPatterns();
        if (!CollectionUtils.isEmpty(patterns)) {
            allPattern.putAll(patterns);
        }
        // 注意：patterns中的key与pattern的key重复，patterns中的不生效（Map无重复Key）
        if (!CollectionUtils.isEmpty(pattern)) {
            allPattern.putAll(pattern);
        }
        return allPattern;
    }

    /**
     * 获取key为patterns的值
     * 
     * @return patterns对应的map，或者null（如patterns=123这种情况）
     */
    public static Map<String, Object> getPatterns() {
        Map<String, Object> map = new HashMap<String, Object>();
        Object patterns = patternMap.get(PATTERNS);
        // patterns下有多个key的时候(List)
        if (patterns instanceof List) {
            // 获取key为"patterns"的值(List<Map<String, Object>>)
            List<Map<String, Object>> list = (List<Map<String, Object>>) patterns;
            if (!CollectionUtils.isEmpty(list)) {
                Iterator<Map<String, Object>> iterator = list.iterator();
                // 黄线强迫症，用for代替while
                for (; iterator.hasNext();) {
                    Map<String, Object> maps = (Map<String, Object>) iterator.next();
                    assembleMap(map, maps);
                }
                return map;
            }
        }
        // patterns只有一个key的时候，且非List
        if (patterns instanceof Map) {
            assembleMap(map, (Map<String, Object>) patterns);
            return map;
        } else {
            return null;
        }
    }

    /**
     * 将patterns中每个key对应的规则按<key,规则>的方式放入map
     * 
     * @param map
     *            map
     * @param patterns
     *            patterns
     */
    private static void assembleMap(Map<String, Object> map, Map<String, Object> patterns) {
        // 获取patterns里key值为"key"的值(脱敏关键字)
        Object key = patterns.get("key");
        if (key instanceof String) {
            // 清除空格
            String keyWords = ((String) key).replace(" ", "");
            // 以逗号分隔出一个key数组
            String[] keyArr = keyWords.split(",");
            for (String keyStr : keyArr) {
                map.put(keyStr, patterns.get(CUSTOM));
            }
        }
    }

    /**
     * 是否开启脱敏，默认不开启
     * 
     * @return 是否开启脱敏
     */
    public static Boolean getOpen() {
        Object flag = patternMap.get(OPEN_FLAG);
        return flag instanceof Boolean ? (Boolean) flag : false;
    }

    /**
     * 是否忽略大小写匹配，默认开启
     * 
     * @return 是否忽略大小写匹配
     */
    public static Boolean getIgnore() {
        Object flag = patternMap.get(IGNORE);
        return flag instanceof Boolean ? (Boolean) flag : true;
    }
}
