package com.sz.wechat.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.HttpRedirects;
import org.springframework.boot.http.client.InetAddressFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * 微信外部 API 专用 HTTP 客户端配置。
 */
@Configuration(proxyBeanMethods = false)
public class WechatRestClientConfiguration {

    public static final String WECHAT_REST_CLIENT = "wechatRestClient";

    @Bean(WECHAT_REST_CLIENT)
    public RestClient wechatRestClient(RestClient.Builder autoConfiguredBuilder) {
        return autoConfiguredBuilder.clone().requestFactory(ClientHttpRequestFactoryBuilder.jdk().build(wechatHttpClientSettings())).build();
    }

    static HttpClientSettings wechatHttpClientSettings() {
        return HttpClientSettings.defaults().withTimeouts(Duration.ofSeconds(3), Duration.ofSeconds(30)).withRedirects(HttpRedirects.DONT_FOLLOW)
                .withInetAddressFilter(InetAddressFilter.externalAddresses());
    }
}
