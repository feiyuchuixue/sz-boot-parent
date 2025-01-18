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
 * @author sz
 * @since 2023/12/25 10:01
 */
public class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("FileUtils class Illegal");
    }

    /**
     * 下载模板文件并写入到响应输出流中。
     * <p>
     * 该方法从指定路径加载模板文件，并将其内容写入到 `HttpServletResponse` 的输出流中，供客户端下载。
     * </p>
     *
     * @param resourceLoader
     *            用于加载资源的 `ResourceLoader`
     * @param response
     *            HTTP 响应对象
     * @param templateFileName
     *            模板文件名
     * @throws IOException
     *             如果在读取模板文件或写入响应时发生 I/O 错误
     */
    public static void downloadTemplateFile(ResourceLoader resourceLoader, HttpServletResponse response, String templateFileName) throws IOException {
        String templatePath = "classpath:/templates/" + templateFileName;
        Resource resource = resourceLoader.getResource(templatePath);
        OutputStream os = getOutputStream(response, templateFileName);
        InputStream inputStream = resource.getInputStream();
        FileCopyUtils.copy(inputStream, os);
        os.flush();
    }

    /**
     * 检查给定的磁盘路径是否存在且为目录。
     * <p>
     * 该方法用于验证指定路径是否存在并且是一个目录。
     * </p>
     *
     * @param path
     *            待检查的路径
     * @return 如果路径存在且为目录返回 `true`，否则返回 `false`
     */
    public static boolean isPathExists(String path) {
        if (path == null || path.trim().isEmpty() || path.isEmpty()) {
            return false;
        }
        Path pathObj = Paths.get(path);
        return Files.exists(pathObj) && Files.isDirectory(pathObj);
    }

    /**
     * 获取响应的 `OutputStream`，用于文件下载。
     * <p>
     * 该方法设置响应头，以支持文件下载，并返回 `ServletOutputStream`。
     * </p>
     *
     * @param response
     *            HTTP 响应对象
     * @param fileName
     *            文件名，例如：教师统计.xlsx
     * @return 用于写入文件内容的 `OutputStream`
     * @throws IOException
     *             如果在获取输出流时发生 I/O 错误
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
     * 获取响应的 `OutputStream`，用于文件下载，并可设置内容长度。
     * <p>
     * 该方法设置响应头，以支持文件下载，并返回 `ServletOutputStream`，还可以指定内容长度。
     * </p>
     *
     * @param response
     *            HTTP 响应对象
     * @param fileName
     *            文件名，例如：教师统计.xlsx
     * @param contentLength
     *            文件内容长度（可选）
     * @return 用于写入文件内容的 `OutputStream`
     * @throws IOException
     *             如果在获取输出流时发生 I/O 错误
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
