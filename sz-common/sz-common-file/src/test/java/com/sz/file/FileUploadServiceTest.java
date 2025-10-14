package com.sz.file;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件上传服务测试类
 */
@SpringBootTest
class FileUploadServiceTest {

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Test
    void testUpload() throws IOException {
        // 创建一个模拟的文件
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        // 测试基础上传
        UploadResult result = fileUploadService.upload(mockFile);
        
        assertNotNull(result);
        assertNotNull(result.getFilename());
        assertNotNull(result.getUrl());
        assertEquals("test.txt", result.getOriginalFilename());
        assertEquals("text/plain", result.getContentType());
        assertEquals(".txt", result.getExtension());
        assertEquals(11, result.getSize());
        
        // 验证文件是否真的被创建
        Path uploadedFile = Paths.get(result.getPath());
        assertTrue(Files.exists(uploadedFile));
        
        // 清理测试文件
        Files.deleteIfExists(uploadedFile);
    }

    @Test
    void testUploadWithPath() throws IOException {
        // 创建一个模拟的文件
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-path.txt",
                "text/plain",
                "Hello World With Path".getBytes()
        );

        // 测试指定路径上传
        String customPath = "test/custom/";
        UploadResult result = fileUploadService.upload(mockFile, customPath);
        
        assertNotNull(result);
        assertTrue(result.getPath().contains(customPath));
        assertEquals("test-path.txt", result.getOriginalFilename());
        assertEquals(20, result.getSize());
        
        // 验证文件是否真的被创建
        Path uploadedFile = Paths.get(result.getPath());
        assertTrue(Files.exists(uploadedFile));
        
        // 清理测试文件和目录
        Files.deleteIfExists(uploadedFile);
        Files.deleteIfExists(uploadedFile.getParent());
    }

    @Test
    void testProperties() {
        // 测试配置属性是否正确加载
        assertNotNull(fileUploadProperties);
        assertNotNull(fileUploadProperties.getBasePath());
        assertTrue(fileUploadProperties.getMaxFileSize() > 0);
    }
}