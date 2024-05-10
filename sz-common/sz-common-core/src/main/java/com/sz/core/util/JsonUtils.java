package com.sz.core.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sz.core.common.entity.TransferMessage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: 弃用fastjson，首选jackson
 * @author: sz
 * @date: 2022/10/8 18:59
 * @description:
 */
public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = SpringApplicationContextUtils.getBean(ObjectMapper.class);

    public static String readJsonFile(String filePath) {
        String jsonStr = "";
        try {
            File jsonFile = new File(filePath);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public static void main(String[] args) throws JsonProcessingException {
        String validJson = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        String invalidJson = "invalid JSON string";
        String emptyJson = "";
        String nullJson = null;
        String nonJson = "12 3";

        System.out.println("Is valid JSON: " + isJsonValid(validJson)); // 输出 true
        System.out.println("Is valid JSON: " + isJsonValid(invalidJson)); // 输出 false
        System.out.println("Is valid JSON: " + isJsonValid(emptyJson)); // 输出 false
        System.out.println("Is valid JSON: " + isJsonValid(nullJson)); // 输出 false
        System.out.println("Is valid JSON: " + isJsonValid(nonJson)); // 输出 false

        String jsonString = "{\"notifyUsers\":[\"admin\"],\"message\":{\"data\":\"\",\"channel\":\"KICK_OFF\"}}";

        // 使用 Jackson 库将 JSON 字符串转换成 Java 对象
        ObjectMapper objectMapper = new ObjectMapper();
        TransferMessage myObject = objectMapper.readValue(jsonString, TransferMessage.class);
        System.out.println("aaabb ==" + JSON.toJSONString(myObject));

    }


}
