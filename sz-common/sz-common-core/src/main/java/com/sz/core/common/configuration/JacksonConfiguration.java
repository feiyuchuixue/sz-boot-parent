package com.sz.core.common.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sz.core.common.entity.MultipartFileSerializer;
import com.sz.core.common.jackson.EmptySerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
@JsonComponent
@Slf4j
public class JacksonConfiguration extends JsonSerializer<LocalDateTime> {

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

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        return builder -> {
            builder.modules(javaTimeModule);
            builder.simpleDateFormat(DATE_TIME_PATTERN);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        };
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.setTimeZone(TimeZone.getTimeZone(TIME_ZONE)); // 指定时区为：中国时
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 针对实体映射时不匹配类型或序列化的处理配置：即找不到、不可用也不抛出异常。
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(new BeanSerializerModifier() {

            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
                for (BeanPropertyWriter writer : beanProperties) {
                    JavaType type = writer.getType();

                    if (type.isTypeOrSubTypeOf(String.class)) {
                        writer.assignNullSerializer(EmptySerializer.EmptyStringSerializer.INSTANCE);
                    } else if (type.isCollectionLikeType()) {
                        writer.assignNullSerializer(EmptySerializer.EmptyArraySerializer.INSTANCE);
                    } else if (type.isMapLikeType()) {
                        writer.assignNullSerializer(EmptySerializer.EmptyObjectSerializer.INSTANCE);
                    } else {
                        // default fallback
                        writer.assignNullSerializer(EmptySerializer.EmptyStringSerializer.INSTANCE);
                    }
                }
                return beanProperties;
            }
        }));
        // 对MultipleFile序列化的支持
        SimpleModule module = new SimpleModule();
        module.addSerializer(MultipartFile.class, new MultipartFileSerializer());
        // 添加Long类型的自定义序列化器
        module.addSerializer(Long.class, new JsonSerializer<>() {

            @Override
            public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value > JS_SAFE_INTEGER_MAX || value < JS_SAFE_INTEGER_MIN) {
                    gen.writeString(value.toString());
                } else {
                    gen.writeNumber(value);
                }
            }
        });
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String formattedValue = value.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        gen.writeString(formattedValue);
    }
}
