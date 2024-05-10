package com.sz.wechat;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @ClassName WechatRestTemplateConfiguration
 * @Author sz
 * @Date 2024/4/26 9:57
 * @Version 1.0
 */
@Deprecated
@Component
public class WechatRestTemplateConfiguration {

    @Bean(name = "wechatRestTemplate")
    public RestTemplate wechatRestTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(60))
                .setReadTimeout(Duration.ofSeconds(60))
                // .errorHandler(new GMatchCustomErrorHandler())
                .build();
        return restTemplate;
    }
}
