package com.sz.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    /**
     * 默认上传路径
     */
    private String basePath = "uploads/";

    /**
     * 最大文件大小 (默认10MB)
     */
    private long maxFileSize = 1024 * 1024 * 10;

    /**
     * 是否允许覆盖同名文件
     */
    private boolean allowOverwrite = false;
}