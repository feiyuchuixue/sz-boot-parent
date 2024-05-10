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
@ConfigurationProperties(prefix = "generator")
public class GeneratorProperties {

    // 初始值配置
    private PathProperties path;

    @Data
    public static class PathProperties {
        private String web; // 前端项目初始路径
        private String api; // 后端项目初始路径
    }

}
