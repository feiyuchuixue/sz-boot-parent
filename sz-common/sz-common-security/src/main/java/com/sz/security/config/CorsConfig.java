package com.sz.security.config;

import com.sz.security.core.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CorsConfig
 * 
 * @author sz
 * @since 2024/2/2 8:31
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final CorsProperties corsProperties;

    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>(corsFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 在生产环境中，推荐将 origin 设置为准确的域名，而不是使用 *
        CopyOnWriteArrayList<String> allowedOrigins = corsProperties.getAllowedOrigins();
        if (allowedOrigins.contains("*")) {
            corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        } else {
            for (String origin : allowedOrigins) {
                corsConfiguration.addAllowedOrigin(origin);
            }
        }
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.addExposedHeader(CorsConfiguration.ALL);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }
}
