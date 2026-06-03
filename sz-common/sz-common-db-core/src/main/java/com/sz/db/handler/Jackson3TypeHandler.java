package com.sz.db.handler;

import com.mybatisflex.core.handler.BaseJsonTypeHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collection;
import java.util.Map;

/**
 * Jackson 3 JSON column TypeHandler.
 * <p>
 * MyBatis-Flex built-in {@code JacksonTypeHandler} depends on the Jackson 2
 * {@code com.fasterxml.jackson.*} package. The project runs on Spring Boot 4
 * and Jackson 3, so JSON columns should use this handler instead.
 */
public class Jackson3TypeHandler extends BaseJsonTypeHandler<Object> {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).build();

    private final JavaType javaType;

    public Jackson3TypeHandler(Class<?> type) {
        this.javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
    }

    @SuppressWarnings("unchecked")
    public Jackson3TypeHandler(Class<?> type, Class<?> genericType) {
        if (Collection.class.isAssignableFrom(type)) {
            this.javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType((Class<? extends Collection>) type, genericType);
        } else if (Map.class.isAssignableFrom(type)) {
            this.javaType = OBJECT_MAPPER.getTypeFactory().constructMapType((Class<? extends Map>) type, String.class, genericType);
        } else {
            this.javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(type, genericType);
        }
    }

    @Override
    protected Object parseJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Deserialize JSON column failed", e);
        }
    }

    @Override
    protected String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Serialize JSON column failed", e);
        }
    }
}
