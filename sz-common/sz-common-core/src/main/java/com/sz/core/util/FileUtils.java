package com.sz.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.UriUtils;

import java.io.*;
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

        if (resource.exists()) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString()); // 设置响应字符编码
            setAttachmentResponseHeader(response, templateFileName);

            try (InputStream inputStream = resource.getInputStream();
                 OutputStream outputStream = response.getOutputStream()) {
                FileCopyUtils.copy(inputStream, outputStream);
                outputStream.flush();
            }
        } else {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            PrintWriter writer = response.getWriter();
            ApiResult<Object> error = ApiResult.error(CommonResponseEnum.FILE_NOT_EXISTS);
            ObjectMapper objectMapper = new ObjectMapper();
            writer.write(objectMapper.writeValueAsString(error));
            writer.flush();
        }
    }

    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) {
        String percentEncodedFileName = encodeName(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder()
                .append("attachment; filename=")
                .append(percentEncodedFileName)
                .append("; filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    public static String encodeName(String name) {
        return UriUtils.encode(name, StandardCharsets.UTF_8.toString());
    }

    /**
     * 检查磁盘路径是否存在
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
}
