package com.sz.core.common.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 *
 * @author sz
 * @version 1.0
 * @since 2022/8/29
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    private final ObjectProvider<HttpMessageConverters> messageConverters;

    public WebMvcConfiguration(ObjectProvider<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * 两种解决方案: - 一、 使用 WebMvcConfigurer 而非WebMvcConfigurationSupport - 二、 使用此方法
     * <p>
     * 参考WebMvcAutoConfiguration类中configureMessageConverters方法，使Jackson配置生效
     *
     * @param converters
     *            消息转换器
     */
    @Override
    protected void configureMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        this.messageConverters.ifAvailable(customConverters -> converters.addAll(customConverters.getConverters()));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/", "classpath:/META-INF/resources/");
    }
}