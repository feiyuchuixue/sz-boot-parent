package com.sz.core.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 提供针对不同类型的 Jackson null 值序列化器，可用于精细化控制字段为 {@code null} 时的序列化行为。
 * <p>
 * 通常搭配 {@code BeanSerializerModifier} 使用，根据字段类型指定对应的 null 值序列化策略， 用于替代默认的
 * {@code null} 输出，提升前端兼容性和可读性。
 * </p>
 *
 * <p>
 * 典型应用：
 * </p>
 * <ul>
 * <li>String 类型 null → ""</li>
 * <li>List/Set 类型 null → []</li>
 * <li>Map 类型 null → {}</li>
 * <li>Number 类型 null → 0</li>
 * <li>Boolean 类型 null → false</li>
 * </ul>
 *
 * <p>
 * 示例用法（配合 BeanSerializerModifier）：
 * 
 * <pre>{@code
 * if (type.isTypeOrSubTypeOf(String.class)) {
 *     writer.assignNullSerializer(EmptyStringSerializer.INSTANCE);
 * }
 * }</pre>
 * </p>
 *
 * @author sz
 * @since 2025/7/14
 */
public class EmptySerializer {

    public static class EmptyStringSerializer extends JsonSerializer<Object> {

        public static final EmptyStringSerializer INSTANCE = new EmptyStringSerializer();
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString("");
        }
    }

    public static class EmptyArraySerializer extends JsonSerializer<Object> {

        public static final EmptyArraySerializer INSTANCE = new EmptyArraySerializer();
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartArray();
            gen.writeEndArray();
        }
    }

    public static class EmptyObjectSerializer extends JsonSerializer<Object> {

        public static final EmptyObjectSerializer INSTANCE = new EmptyObjectSerializer();
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeEndObject();
        }
    }

    public static class ZeroNumberSerializer extends JsonSerializer<Object> {

        public static final ZeroNumberSerializer INSTANCE = new ZeroNumberSerializer();
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeNumber(0);
        }
    }

    public static class FalseBooleanSerializer extends JsonSerializer<Object> {

        public static final FalseBooleanSerializer INSTANCE = new FalseBooleanSerializer();
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeBoolean(false);
        }
    }
}
