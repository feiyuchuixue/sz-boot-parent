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

/**
 * @author: sz
 * @date: 2022/10/8 18:59
 * @description:
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = SpringApplicationContextUtils.getBean(ObjectMapper.class);

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

    public static String toJsonString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonStringPretty(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, Class<?> parametrized, Class<?>... parameterClasses) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> jsonToMap(String jsonString) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseArray(String text, TypeReference<List<T>> typeReference) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}