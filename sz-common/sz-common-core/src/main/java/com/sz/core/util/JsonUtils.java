package com.sz.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.xiaoymin.knife4j.core.util.StrUtil.isBlank;

/**
 * @author sz
 * @since 2022/10/8 18:59
 */
@Slf4j
public class JsonUtils {

    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final ObjectMapper OBJECT_MAPPER = SpringApplicationContextUtils.getInstance().getBean(ObjectMapper.class);

    /**
     * 读取指定路径的 JSON 文件并返回其内容。
     *
     * @param filePath
     *            JSON 文件的路径
     * @return 文件内容的字符串表示，如果读取失败返回 null
     */
    public static String readJsonFile(String filePath) {
        String jsonStr;
        try {
            File jsonFile = new File(filePath);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (Exception ex) {
            log.error("readJsonFile error: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * 校验给定的字符串是否是有效的 JSON 格式。
     *
     * @param jsonString
     *            待验证的 JSON 字符串
     * @return 如果是有效的 JSON 返回 true，否则返回 false
     */
    public static boolean isJsonValid(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return false; // 空字符串不是有效的 JSON
        }
        try {
            // 使用 JsonNodeFactory 创建一个虚拟的 JSON 对象
            JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
            ObjectNode rootNode = nodeFactory.objectNode();
            // 尝试解析 JSON，如果解析失败会抛出异常
            OBJECT_MAPPER.readerForUpdating(rootNode).readValue(jsonString);
            return true; // 解析成功，说明是有效的 JSON
        } catch (Exception e) {
            return false; // 解析失败，不是有效的 JSON
        }
    }

    /**
     * 将对象转换为 JSON 字符串。
     *
     * @param object
     *            要转换的对象
     * @return 对象的 JSON 字符串表示，如果对象为 null 返回 null
     */
    public static String toJsonString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将对象转换为格式化的 JSON 字符串。
     *
     * @param object
     *            要转换的对象
     * @return 对象的格式化 JSON 字符串表示，如果对象为 null 返回 null
     */
    public static String toJsonStringPretty(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象。
     *
     * @param <T>
     *            目标对象的类型
     * @param text
     *            JSON 字符串
     * @param clazz
     *            目标对象的类
     * @return 解析后的对象，如果解析失败返回 null
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 使用类型引用将 JSON 字符串解析为对象。
     *
     * @param <T>
     *            目标对象的类型
     * @param text
     *            JSON 字符串
     * @param typeReference
     *            类型引用
     * @return 解析后的对象，如果解析失败返回 null
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (isBlank(text)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为带泛型参数的对象。
     *
     * @param <T>
     *            目标对象的类型
     * @param text
     *            JSON 字符串
     * @param parametrized
     *            目标对象的类
     * @param parameterClasses
     *            泛型参数类
     * @return 解析后的对象，如果解析失败返回 null
     */
    public static <T> T parseObject(String text, Class<?> parametrized, Class<?>... parameterClasses) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将 JSON 字符串转换为 Map 对象。
     *
     * @param jsonString
     *            JSON 字符串
     * @return 包含键值对的 Map，如果解析失败抛出异常
     */
    public static Map<String, Object> jsonToMap(String jsonString) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的列表。
     *
     * @param <T>
     *            列表元素的类型
     * @param text
     *            JSON 字符串
     * @param clazz
     *            列表元素的类
     * @return 解析后的列表，如果解析失败返回空列表
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 使用类型引用将 JSON 字符串解析为列表。
     *
     * @param <T>
     *            列表元素的类型
     * @param text
     *            JSON 字符串
     * @param typeReference
     *            类型引用
     * @return 解析后的列表，如果解析失败返回空列表
     */
    public static <T> List<T> parseArray(String text, TypeReference<List<T>> typeReference) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}