package com.sz.core.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * Jackson 反序列化器：用于将空字符串（""）反序列化为一个空的 {@link Map}。
 * <p>
 * 当后端字段为 {@code Map<?, ?>} 类型，但前端传入的是空字符串（如 {@code ""}）， 默认 Jackson
 * 会抛出反序列化异常。此类可用于兼容这种情况，将空字符串安全地转为 {@code new HashMap<>()}。
 * </p>
 *
 * <p>
 * 使用方式：
 * </p>
 * 
 * <pre>{@code
 * 
 * @JsonDeserialize(using = EmptyStringAsEmptyMapDeserializer.class)
 * private Map<String, Object> extraParams;
 * }</pre>
 *
 * <p>
 * 推荐搭配字段级使用
 * </p>
 *
 * @author sz
 * @since 2025/7/14
 */
public class EmptyStringAsEmptyMapDeserializer extends JsonDeserializer<Map<?, ?>> {

    @Override
    public Map<?, ?> deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        if (parser.getCurrentToken().isScalarValue() && "".equals(parser.getText())) {
            return new HashMap<>();
        }
        // 其他情况下走默认处理
        return ctx.readValue(parser, Map.class);
    }
}