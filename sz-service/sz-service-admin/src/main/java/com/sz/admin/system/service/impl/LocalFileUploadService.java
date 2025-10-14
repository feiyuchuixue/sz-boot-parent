package com.sz.admin.system.service.impl;

import com.sz.core.util.StringUtils;
import com.sz.oss.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件上传服务
 * 用于替代OSS客户端进行本地测试
 */
@Slf4j
@Service
public class LocalFileUploadService {

    @Value("${file.upload.base-path:uploads/}")
    private String basePath;

    /**
     * 上传文件到本地
     *
     * @param file   文件
     * @param dirTag 目录标签
     * @return 上传结果
     * @throws IOException IO异常
     */
    public UploadResult upload(MultipartFile file, String dirTag) throws IOException {
        // 确保路径以斜杠结尾
        if (!dirTag.endsWith("/")) {
            dirTag += "/";
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String filename = generateFileName(originalFilename);
        
        // 构建完整路径到项目目录外的uploads文件夹
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePath = df.format(LocalDate.now());
        
        // 获取项目根目录的父目录，然后定位到 uploads 文件夹
        String userDir = System.getProperty("user.dir");
        Path projectDir = Paths.get(userDir);
        Path parentDir = projectDir.getParent();
        Path uploadDir = parentDir.resolve("uploads").resolve(dirTag).resolve(datePath);
        
        // 创建目录
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 处理文件名冲突
        Path filePath = uploadDir.resolve(filename);
        if (Files.exists(filePath)) {
            // 如果文件已存在，则生成新的文件名
            filename = generateUniqueFilename(uploadDir, originalFilename);
            filePath = uploadDir.resolve(filename);
        }

        // 保存文件
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 构建返回结果
        UploadResult result = UploadResult.builder()
                .filename(filename)
                .objectName(dirTag + datePath + "/" + filename)
                .dirTag(dirTag)
                .size(file.getSize())
                .contextType(file.getContentType())
                .eTag(UUID.randomUUID().toString()) // 生成一个随机ETag
                .build();
        
        // 构建访问URL，修复双斜杠问题
        String url = "/uploads/" + (dirTag.startsWith("/") ? dirTag.substring(1) : dirTag) + datePath + "/" + filename;
        result.setUrl(url);
        
        log.info("文件上传成功: {}", filePath.toString());
        return result;
    }

    /**
     * 生成文件名
     *
     * @param originalFilename 原始文件名
     * @return 生成的文件名
     */
    private String generateFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            return UUID.randomUUID().toString();
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + ext;
    }

    /**
     * 生成唯一文件名
     *
     * @param uploadPath       上传路径
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFilename(Path uploadPath, String originalFilename) {
        String filename = generateFileName(originalFilename);
        Path filePath = uploadPath.resolve(filename);

        // 如果文件仍存在，则添加序号
        int counter = 1;
        while (Files.exists(filePath)) {
            String nameWithoutExt = filename;
            String ext = "";
            
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                nameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            }
            
            filename = nameWithoutExt + "_" + counter + ext;
            filePath = uploadPath.resolve(filename);
            counter++;
        }

        return filename;
    }
}