package com.sz.platform.listener;

import com.sz.redis.RedisTemplateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author sz
 * @since 2022/9/9 16:21
 * 
 */
@Slf4j
@Component
@Order(value = 1)
@RequiredArgsConstructor
public class AppStartListener implements CommandLineRunner, RedisTemplateClient {

    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info(" ===================== app is running finish ... =====================");
    }

    @Override
    public RedisTemplate<Object, Object> getTemplate() {
        return redisTemplate;
    }
}
