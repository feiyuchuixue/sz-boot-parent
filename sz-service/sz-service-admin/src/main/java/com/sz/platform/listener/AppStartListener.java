package com.sz.platform.listener;

import com.sz.redis.RedisTemplateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: sz
 * @date: 2022/9/9 16:21
 * @description:
 */
@Slf4j
@Component
@Order(value = 1)
@RequiredArgsConstructor
public class AppStartListener implements CommandLineRunner, RedisTemplateClient {

    private final RedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info(" ===================== app is running finish ... =====================");

        // TODO something
    }

    @Override
    public RedisTemplate getTemplate() {
        return redisTemplate;
    }
}
