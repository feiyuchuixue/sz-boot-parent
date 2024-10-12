package com.sz.core.common.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author: sz
 * @date: 2022/8/29 11:10
 * @description: WebMvc配置, 注意这里，实现@WebMvcConfigurer会导致全局jackson配置失效
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport implements WebMvcRegistrations {

    @Autowired
    private ObjectProvider<HttpMessageConverters> messageConverters;

    /**
     * 两种解决方案: - 一、 使用 WebMvcConfigurer 而非WebMvcConfigurationSupport - 二、 使用此方法
     * <p>
     * 参考WebMvcAutoConfiguration类中configureMessageConverters方法，使Jackson配置生效
     *
     * @param converters
     */

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        this.messageConverters.ifAvailable((customConverters) -> converters.addAll(customConverters.getConverters()));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").addResourceLocations("classpath:/META-INF/resources/");
    }

}
