package com.sz.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

/**
 * @author sz
 * @since 2022/6/4 11:01
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // 使用 Jackson 3 自定义序列化器替换默认序列化
        RedisSerializer<Object> jackson3Serializer = buildJackson3Serializer();
        // 设置value的序列化规则和 key的序列化规则
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson3Serializer);
        // 设置hash的序列化规则
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson3Serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 构建基于 Jackson 3 的 Redis 序列化器 开启 NON_FINAL 多态类型信息，确保反序列化时可还原完整类型
     */
    private RedisSerializer<Object> buildJackson3Serializer() {
        ObjectMapper om = JsonMapper.builder().changeDefaultVisibility(v -> v.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
                // 开启多态类型信息（NON_FINAL），用于 Redis 序列化时保留完整类型
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(), DefaultTyping.NON_FINAL).build();

        return new RedisSerializer<>() {

            @Override
            public byte[] serialize(Object value) throws SerializationException {
                if (value == null) {
                    return new byte[0];
                }
                try {
                    return om.writeValueAsBytes(value);
                } catch (JacksonException e) {
                    throw new SerializationException("Redis 序列化失败", e);
                }
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                try {
                    return om.readValue(bytes, Object.class);
                } catch (JacksonException e) {
                    throw new SerializationException("Redis 反序列化失败: " + new String(bytes, StandardCharsets.UTF_8), e);
                }
            }
        };
    }

}
