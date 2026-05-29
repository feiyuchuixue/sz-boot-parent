package com.sz.core.common.configuration;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import com.sz.core.common.entity.MultipartFileSerializer;
import com.sz.core.common.jackson.EmptySerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

/**
 * （Jackson）全局统一格式化处理
 * <p>
 * - Null 转为 空字符串 - date 格式化 - 时区 - 驼峰-蛇形转换
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2022/8/26
 */
@Configuration
@Slf4j
public class JacksonConfiguration {

    private static final String TIME_ZONE = "GMT+8";

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private static final long JS_SAFE_INTEGER_MAX = 9007199254740991L;

    private static final long JS_SAFE_INTEGER_MIN = -9007199254740991L;

    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new Converter<String, LocalDate>() {

            @Override
            public LocalDate convert(String source) {
                if (source.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(source);
                } catch (Exception e) {
                    return LocalDate.parse(source, DateTimeFormatter.ofPattern(DATE_PATTERN));
                }
            }
        };
    }

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new Converter<String, LocalDateTime>() {

            @Override
            public LocalDateTime convert(String source) {
                if (source.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDateTime.parse(source);
                } catch (Exception e) {
                    return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
                }
            }
        };
    }

    /**
     * 通过 JsonMapperBuilderCustomizer 将自定义序列化规则注入 SB4 自动配置的 ObjectMapper， 避免注册额外
     * ObjectMapper bean 导致 primary 冲突。
     */
    @Bean
    public JsonMapperBuilderCustomizer szJacksonCustomizer() {
        return (JsonMapper.Builder builder) -> {
            // 时间序列化/反序列化
            SimpleModule javaTimeModule = new SimpleModule();
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));

            // MultipartFile 序列化支持
            javaTimeModule.addSerializer(MultipartFile.class, new MultipartFileSerializer());

            // Long 类型防 JS 精度丢失
            javaTimeModule.addSerializer(Long.class, new ValueSerializer<Long>() {

                @Override
                public void serialize(Long value, JsonGenerator gen, SerializationContext serializers) {
                    if (value > JS_SAFE_INTEGER_MAX || value < JS_SAFE_INTEGER_MIN) {
                        gen.writeString(value.toString());
                    } else {
                        gen.writeNumber(value);
                    }
                }
            });

            // Null 字段序列化规则：String→""，集合→[]，Map→{}，其他→""
            SimpleModule nullHandlerModule = new SimpleModule();
            nullHandlerModule.setSerializerModifier(new ValueSerializerModifier() {

                @Override
                public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription.Supplier beanDesc,
                        List<BeanPropertyWriter> beanProperties) {
                    for (BeanPropertyWriter writer : beanProperties) {
                        JavaType type = writer.getType();
                        if (type.isTypeOrSubTypeOf(String.class)) {
                            writer.assignNullSerializer(EmptySerializer.EmptyStringSerializer.INSTANCE);
                        } else if (type.isCollectionLikeType()) {
                            writer.assignNullSerializer(EmptySerializer.EmptyArraySerializer.INSTANCE);
                        } else if (type.isMapLikeType()) {
                            writer.assignNullSerializer(EmptySerializer.EmptyObjectSerializer.INSTANCE);
                        } else if (type.isTypeOrSubTypeOf(Number.class)) {
                            writer.assignNullSerializer(EmptySerializer.ZeroNumberSerializer.INSTANCE);
                        } else if (type.isTypeOrSubTypeOf(Boolean.class)) {
                            writer.assignNullSerializer(EmptySerializer.FalseBooleanSerializer.INSTANCE);
                        } else {
                            writer.assignNullSerializer(EmptySerializer.EmptyStringSerializer.INSTANCE);
                        }
                    }
                    return beanProperties;
                }
            });

            builder.addModule(javaTimeModule).addModule(nullHandlerModule).defaultTimeZone(TimeZone.getTimeZone(TIME_ZONE))
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        };
    }
}
