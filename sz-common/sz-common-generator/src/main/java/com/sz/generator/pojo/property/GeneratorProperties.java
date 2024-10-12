package com.sz.generator.pojo.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName GeneratorProperty
 * @Author sz
 * @Date 2024/1/15 8:38
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "sz.generator")
public class GeneratorProperties {

    {
        moduleName = "sz-service";
        serviceName = "sz-service-admin";
    }

    // 初始值配置
    private PathProperties path;

    // 全局配置
    private GlobalProperties global;

    // 模块名
    private String moduleName;

    // service 名
    private String serviceName;

    @Data
    public static class GlobalProperties {

        {
            author = "sz-admin";
            packages = "com.sz.admin";
        }

        // 作者
        private String author;

        // 包名
        private String packages;
    }

    @Data
    public static class PathProperties {

        // 前端项目初始路径
        private String web;

        // 后端项目初始路径
        private String api;
    }

}
