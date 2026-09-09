package com.sz.logger.desensitize;

import com.sz.core.util.MaskUtils;
import com.sz.logger.utils.DesensitizationUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 统一日志脱敏器，供文件日志、审计入库和诊断兜底共同使用。
 */
@Component
public class LogDesensitizer {

    public static final LogDesensitizer GLOBAL = new LogDesensitizer();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<Object> JSON_TYPE = new TypeReference<>() {
    };

    private static final String MASK_VALUE = "******";

    private static final Set<String> FULL_MASK_KEYS = Set.of("password", "pwd", "token", "accesstoken", "refreshtoken", "secret", "authorization");

    private static final Set<String> PHONE_KEYS = Set.of("phone", "mobile", "cellphone");

    private static final Set<String> EMAIL_KEYS = Set.of("email", "mail");

    private static final Set<String> ID_CARD_KEYS = Set.of("idcard", "identity", "identitycard", "certno");

    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("(?i)(\"?(?:password|pwd|token|accessToken|refreshToken|secret|authorization)\"?\\s*[:=]\\s*\"?)([^\",}\\]\\s]+)");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    public String desensitize(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        Object jsonValue = parseJsonIfPossible(text);
        if (jsonValue instanceof Map<?, ?> || jsonValue instanceof List<?>) {
            return writeJsonSafely(desensitizeJsonValue(null, jsonValue), text);
        }
        return desensitizeText(text);
    }

    private Object parseJsonIfPossible(String text) {
        String value = text.trim();
        if (!isJsonObjectOrArray(value)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(value, JSON_TYPE);
        } catch (JacksonException ignored) {
            return null;
        }
    }

    private boolean isJsonObjectOrArray(String text) {
        return (text.startsWith("{") && text.endsWith("}")) || (text.startsWith("[") && text.endsWith("]"));
    }

    private String writeJsonSafely(Object value, String originalText) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JacksonException ignored) {
            return desensitizeText(originalText);
        }
    }

    private Object desensitizeJsonValue(String fieldName, Object value) {
        if (isFullMaskKey(fieldName)) {
            return MASK_VALUE;
        }
        if (value == null) {
            return null;
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> masked = new LinkedHashMap<>(map.size());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String childFieldName = String.valueOf(entry.getKey());
                masked.put(childFieldName, desensitizeJsonValue(childFieldName, entry.getValue()));
            }
            return masked;
        }
        if (value instanceof List<?> list) {
            List<Object> masked = new ArrayList<>(list.size());
            for (Object item : list) {
                masked.add(desensitizeJsonValue(fieldName, item));
            }
            return masked;
        }
        if (value instanceof String stringValue) {
            return desensitizeStringByField(fieldName, stringValue);
        }
        if (isFieldSensitive(fieldName)) {
            return desensitizeStringByField(fieldName, String.valueOf(value));
        }
        return value;
    }

    private String desensitizeStringByField(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String normalizedFieldName = normalizeFieldName(fieldName);
        if (PHONE_KEYS.contains(normalizedFieldName)) {
            return MaskUtils.maskPhone(value);
        }
        if (EMAIL_KEYS.contains(normalizedFieldName)) {
            return MaskUtils.maskEmail(value);
        }
        if (ID_CARD_KEYS.contains(normalizedFieldName)) {
            return MaskUtils.maskIdCard(value);
        }
        return value;
    }

    private boolean isFieldSensitive(String fieldName) {
        String normalizedFieldName = normalizeFieldName(fieldName);
        return PHONE_KEYS.contains(normalizedFieldName) || EMAIL_KEYS.contains(normalizedFieldName) || ID_CARD_KEYS.contains(normalizedFieldName);
    }

    private boolean isFullMaskKey(String fieldName) {
        return FULL_MASK_KEYS.contains(normalizeFieldName(fieldName));
    }

    private String normalizeFieldName(String fieldName) {
        if (fieldName == null) {
            return "";
        }
        return fieldName.replace("_", "").replace("-", "").toLowerCase(Locale.ROOT);
    }

    private String desensitizeText(String text) {
        String value = text;
        try {
            String legacy = new DesensitizationUtil().customChange(value);
            if (legacy != null && !legacy.isBlank()) {
                value = legacy;
            }
        } catch (Exception ignored) {
            // 旧脱敏规则失败时继续使用安全兜底规则，不能让脱敏影响业务日志。
        }
        value = replacePasswordLike(value);
        return replaceEmailLike(value);
    }

    private String replacePasswordLike(String text) {
        Matcher matcher = PASSWORD_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(builder, Matcher.quoteReplacement(matcher.group(1) + MASK_VALUE));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String replaceEmailLike(String text) {
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(builder, Matcher.quoteReplacement(MaskUtils.maskEmail(matcher.group())));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }
}
