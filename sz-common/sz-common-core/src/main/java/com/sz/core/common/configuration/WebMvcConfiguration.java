package com.sz.core.common.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;

/**
 *
 * @author sz
 * @version 1.0
 * @since 2022/8/29
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<HttpMessageConverters> messageConverters;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 将你自定义的转换器加到现有列表中
        this.messageConverters.ifAvailable(customConverters -> converters.addAll(customConverters.getConverters()));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        
        // 添加本地上传文件的资源映射
        addLocalUploadResourceHandler(registry);
    }
    
    /**
     * 添加本地上传文件的资源处理器
     * @param registry 资源处理器注册器
     */
    private void addLocalUploadResourceHandler(ResourceHandlerRegistry registry) {
        try {
            // 获取项目根目录的父目录，然后定位到 uploads 文件夹
            String userDir = System.getProperty("user.dir");
            File projectDir = new File(userDir);
            File parentDir = projectDir.getParentFile();
            
            // 在项目根目录的同级创建或定位 uploads 文件夹
            File uploadDir = new File(parentDir, "uploads");
            
            // 确保目录存在
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                log.info("创建上传目录: {}", uploadDir.getAbsolutePath());
            }
            
            // 构建正确的文件协议路径
            String uploadPath = uploadDir.getAbsolutePath();
            // 确保路径以文件分隔符结尾
            if (!uploadPath.endsWith(File.separator)) {
                uploadPath += File.separator;
            }
            
            // 转换为URL格式（统一使用正斜杠，并添加file协议）
            String fileUrlPath = "file:///" + uploadPath.replace("\\", "/");
            
            log.info("注册本地上传资源处理器: /uploads/** -> {}", fileUrlPath);
            
            // 映射URL路径到本地文件系统路径
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(fileUrlPath);
        } catch (Exception e) {
            log.error("注册本地上传资源处理器失败", e);
        }
    }
}