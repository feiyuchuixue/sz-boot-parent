package com.sz.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务实现类
 * 实现了三种不同的文件上传方式
 *
 * @author sz
 * @since 2025-10-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadProperties fileUploadProperties;

    @Override
    public UploadResult upload(MultipartFile file) throws IOException {
        return upload(file, fileUploadProperties.getBasePath());
    }

    @Override
    public UploadResult upload(MultipartFile file, String path) throws IOException {
        return upload(file, path, fileUploadProperties.isAllowOverwrite());
    }

    @Override
    public UploadResult upload(MultipartFile file, String path, boolean overwrite) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > fileUploadProperties.getMaxFileSize()) {
            throw new IllegalArgumentException("文件大小超过限制");
        }

        // 确保路径以斜杠结尾
        if (!path.endsWith("/")) {
            path += "/";
        }

        // 创建完整路径
        String originalFilename = file.getOriginalFilename();
        String filename = generateFilename(originalFilename);
        Path uploadPath = Paths.get(path);
        
        // 如果目录不存在则创建
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 处理文件名冲突
        Path filePath = uploadPath.resolve(filename);
        if (Files.exists(filePath) && !overwrite) {
            // 如果文件已存在且不覆盖，则生成新的文件名
            filename = generateUniqueFilename(uploadPath, originalFilename);
            filePath = uploadPath.resolve(filename);
        }

        // 保存文件
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 构建返回结果
        UploadResult result = new UploadResult();
        result.setFilename(filename);
        result.setPath(filePath.toString());
        result.setSize(file.getSize());
        result.setContentType(file.getContentType());
        result.setOriginalFilename(originalFilename);
        result.setUrl("/" + path + filename);
        
        // 设置文件扩展名
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            result.setExtension(originalFilename.substring(originalFilename.lastIndexOf(".")));
        }

        log.info("文件上传成功: {}", filePath.toString());

        return result;
    }

    /**
     * 生成文件名
     *
     * @param originalFilename 原始文件名
     * @return 生成的文件名
     */
    private String generateFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            return UUID.randomUUID().toString();
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + ext;
    }

    /**
     * 生成唯一文件名
     *
     * @param uploadPath 上传路径
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     * @throws IOException IO异常
     */
    private String generateUniqueFilename(Path uploadPath, String originalFilename) throws IOException {
        String filename = generateFilename(originalFilename);
        Path filePath = uploadPath.resolve(filename);

        // 如果文件仍存在，则添加时间戳
        if (Files.exists(filePath)) {
            String nameWithoutExt = filename;
            String ext = "";
            
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                nameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            filename = nameWithoutExt + "_" + timestamp + ext;
        }

        return filename;
    }
}