package com.sz.security.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 后台服务跨域 Origin 配置。CorsConfig 在创建最高优先级 CORS 过滤器时直接读取本配置。
 *
 * @author sz
 * @since 2025/1/6 10:02
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sz.cors")
public class CorsProperties {

    /**
     * 允许跨域访问的 Origin 列表，值会原样交给 Spring CorsConfiguration。必须配置为非 null；缺省会在创建 CORS
     * 过滤器时因 无法读取列表而失败。空列表不允许任何跨域 Origin；列表只要包含精确值 "*"，代码就仅注册通配 Origin 并忽略同列表中的
     * 其他值。未包含星号时逐项精确注册，代码不负责去除单项空白。过滤器同时允许全部请求头和方法，并暴露全部响应头。
     */
    private CopyOnWriteArrayList<String> allowedOrigins;

}
