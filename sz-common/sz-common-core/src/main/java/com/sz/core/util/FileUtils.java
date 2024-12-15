package com.sz.core.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @ClassName FileUtils
 * @Author sz
 * @Date 2023/12/25 10:01
 * @Version 1.0
 */
public class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("FileUtils class Illegal");
    }

    public static void downloadTemplateFile(ResourceLoader resourceLoader, HttpServletResponse response, String templateFileName) throws IOException {
        String templatePath = "classpath:/templates/" + templateFileName;
        Resource resource = resourceLoader.getResource(templatePath);
        OutputStream os = getOutputStream(response, templateFileName);
        InputStream inputStream = resource.getInputStream();
        FileCopyUtils.copy(inputStream, os);
        os.flush();
    }

    /**
     * 检查磁盘路径是否存在
     *
     * @param path
     * @return
     */
    public static boolean isPathExists(String path) {
        if (path == null || path.trim().length() == 0 || path.isEmpty()) {
            return false;
        }
        Path pathObj = Paths.get(path);
        return Files.exists(pathObj) && Files.isDirectory(pathObj);
    }

    /**
     * 获取文件ServletOutputStream
     *
     * @param response
     * @param fileName
     *            文件名，例如：教师统计.xlsx
     * @return
     * @throws IOException
     */
    public static OutputStream getOutputStream(HttpServletResponse response, String fileName) throws IOException {
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        // 设置响应头
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return response.getOutputStream();
    }

    /**
     * 获取文件ServletOutputStream
     *
     * @param response
     * @param fileName
     *            文件名，例如：教师统计.xlsx
     * @return
     * @throws IOException
     */
    public static OutputStream getOutputStream(HttpServletResponse response, String fileName, Integer contentLength) throws IOException {
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        // 设置响应头
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        if (contentLength != null) {
            response.setContentLength(contentLength);
        }
        return response.getOutputStream();
    }
}
