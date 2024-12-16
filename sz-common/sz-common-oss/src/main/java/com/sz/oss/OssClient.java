package com.sz.oss;

import com.sz.core.util.FileUtils;
import com.sz.core.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @ClassName OssClient
 * @Author sz
 * @Date 2024/11/12 14:56
 * @Version 1.0
 */

@Service
@RequiredArgsConstructor
public class OssClient {

    private final OssProperties properties;

    private final S3Client s3Client;

    private final S3TransferManager transferManager;

    private final S3Presigner s3Presigner;

    private final Long DEFAULT_EXPIRE_TIME = 3600L;

    private final S3AsyncClient s3AsyncClient;

    /**
     * 上传文件
     *
     * @param file
     *            文件
     * @param dirTag
     *            目录
     * @return
     * @throws IOException
     */
    public UploadResult upload(MultipartFile file, String dirTag) throws IOException {
        FileNamingEnum naming = properties.getNaming();
        String objectName;
        if (naming.equals(FileNamingEnum.ORIGINAL)) {
            objectName = dirTag + generateOriginalFileName(file.getOriginalFilename());
            if (isFileExists(objectName)) {
                String ext = org.springframework.util.StringUtils.getFilenameExtension(objectName);
                assert ext != null;
                DateTimeFormatter df = DateTimeFormatter.ofPattern("HHmmss.SSS"); // 获取时分秒.毫秒
                String localTime = df.format(LocalDateTime.now());
                objectName = objectName.split("." + ext)[0] + " (" + localTime + ")." + ext;
            }
        } else {
            objectName = dirTag + generateFileName(file.getOriginalFilename());
        }
        return upload(file.getInputStream(), objectName, file.getSize(), file.getContentType());
    }

    /**
     * 上传文件
     *
     * @param file
     *            文件
     * @param dirTag
     *            目录标签
     * @param filename
     *            文件名
     * @return
     * @throws IOException
     */
    public UploadResult upload(MultipartFile file, String dirTag, String filename) throws IOException {
        String objectName = dirTag + "/" + filename;
        return upload(file.getInputStream(), objectName, file.getSize(), file.getContentType());
    }

    /**
     * 上传文件
     *
     * @param inputStream
     *            输入流
     * @param objectName
     *            对象名（唯一，可包含路径）
     * @param size
     *            文件大小
     * @param contextType
     *            文件类型
     * @return
     */
    public UploadResult upload(InputStream inputStream, String objectName, Long size, String contextType) {
        try {
            BlockingInputStreamAsyncRequestBody body = AsyncRequestBody.forBlockingInputStream(size);
            Upload upload = transferManager.upload(x -> x.requestBody(body)
                    .putObjectRequest(y -> y.bucket(properties.getBucketName()).key(objectName).contentType(contextType).build()).build());
            body.writeInputStream(inputStream);
            CompletedUpload uploadResult = upload.completionFuture().join();
            String eTag = normalizeETag(uploadResult.response().eTag());
            String url = getUrl() + "/" + objectName;
            return UploadResult.builder().url(url).objectName(objectName).dirTag(getDirTag(objectName)).filename(getFileName(objectName))
                    .contextType(contextType).size(size).eTag(eTag).build();
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，Message:[" + e.getMessage() + "]");
        }
    }

    /**
     * 验证文件是否存在
     *
     * @param key
     * @return
     */
    public boolean isFileExists(String key) {
        try {
            s3Client.headObject(b -> b.bucket(properties.getBucketName()).key(key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取私有文件链接
     *
     * @param objectName
     *            对象名（唯一，可包含路径）
     * @return
     */
    public String getPrivateUrl(String objectName) {
        URL url = s3Presigner.presignGetObject(x -> x.signatureDuration(Duration.ofSeconds(DEFAULT_EXPIRE_TIME))
                .getObjectRequest(y -> y.bucket(properties.getBucketName()).key(objectName).build()).build()).url();
        return url.toString();
    }

    /**
     * 获取私有文件链接 eg:
     * https://your_domain.com/test/user/20241125/6%E5%AD%97%E5%85%B8%E7%AE%A1%E7%90%86.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20241125T011936Z&X-Amz-SignedHeaders=host&X-Amz-Credential=EBPVrHftB014oEKdR9y8%2F20241125%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Expires=120&X-Amz-Signature=41e40237dc426d7a3e000014183219607cd709a799c9133e3ee61a882b2d3642
     *
     * @param objectName
     *            对象名（唯一，可包含路径）
     * @param second
     *            有效期（秒）
     * @return
     */
    public String getPrivateUrl(String objectName, Long second) {
        URL url = s3Presigner.presignGetObject(x -> x.signatureDuration(Duration.ofSeconds(second))
                .getObjectRequest(y -> y.bucket(properties.getBucketName()).key(objectName).build()).build()).url();
        return url.toString();
    }

    /**
     * 文件下载
     *
     * @param objectName
     * @param outputStream
     */
    public void download(String objectName, OutputStream outputStream) {
        try {
            // 构建请求
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(properties.getBucketName()).key(objectName).build();

            // 下载对象到内存字节缓冲区
            s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).thenAccept(response -> {
                try {
                    // 写入输出流
                    outputStream.write(response.asByteArray());
                    outputStream.flush();
                } catch (Exception e) {
                    throw new RuntimeException("写入输出流失败", e);
                }
            }).join(); // 同步等待完成
        } catch (Exception e) {
            throw new RuntimeException("下载文件失败，Message:[" + e.getMessage() + "]", e);
        }
    }

    /**
     * 文件下载
     *
     * @param objectName
     * @param response
     */
    public void download(String objectName, HttpServletResponse response, String showFileName) {
        try {
            // 构建请求
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(properties.getBucketName()).key(objectName).build();
            String filename = showFileName;
            if (showFileName.isEmpty()) {
                filename = getFileName(objectName);
            }
            OutputStream os = FileUtils.getOutputStream(response, filename);
            // 下载对象到内存字节缓冲区
            s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).thenAccept(responseBytes -> {
                try {

                    // 写入响应流
                    os.write(responseBytes.asByteArray());
                    os.flush();
                } catch (IOException e) {
                    throw new RuntimeException("写入响应失败", e);
                }
            }).join(); // 同步等待完成
        } catch (Exception e) {
            throw new RuntimeException("下载文件失败，Message:[" + e.getMessage() + "]", e);
        }
    }

    private String getUrl() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String scheme = properties.getIsHttps() ? "https" : "http";
        // 非 MinIO 处理
        if (!properties.getProvider().equals(OssProviderEnum.MINIO)) {
            if (StringUtils.isNotEmpty(domain)) {
                return (domain.startsWith("https://") || domain.startsWith("http://")) ? domain : scheme + domain;
            } else {
                return scheme + properties.getBucketName() + "." + endpoint; // eg: https://your_bucket_name.oss-cn-beijing.aliyuncs.com
            }
        }
        if (StringUtils.isNotEmpty(domain)) {
            return (domain.startsWith("https://") || domain.startsWith("http://"))
                    ? domain + "/" + properties.getBucketName()
                    : scheme + domain + "/" + properties.getBucketName();
        } else {
            return scheme + endpoint + "/" + properties.getBucketName();
        }
    }

    /**
     * 生成文件名 eg: /20231212/4150e7c6-b92f-419d-b804-0e8be80f5e71.png
     *
     * @param originalFilename
     * @return
     */
    private String generateFileName(String originalFilename) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String localTime = df.format(LocalDate.now());
        String pathStr = "/" + localTime + "/";
        String extension = org.springframework.util.StringUtils.getFilenameExtension(originalFilename);
        String fileName = UUID.randomUUID().toString();
        return pathStr + fileName + "." + extension;
    }

    /**
     * 生成文件名 eg: /20231212/logo.jpg
     *
     * @param originalFilename
     * @return
     */
    private String generateOriginalFileName(String originalFilename) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String localTime = df.format(LocalDate.now());
        String pathStr = "/" + localTime + "/";
        return pathStr + originalFilename;
    }

    private String getFileName(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        return (lastSlashIndex != -1) ? path.substring(lastSlashIndex + 1) : path;
    }

    private String getDirTag(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // 找到第一个'/'的位置
        int index = str.indexOf('/');
        // 如果第一个字符是'/'，找到第二个'/'的位置
        if (index == 0) {
            index = str.indexOf('/', index + 1);
        }
        if (index == -1) {
            return str; // 如果没有找到'/'，返回整个字符串
        }
        return str.substring(0, index);
    }

    private String normalizeETag(String eTag) {
        if (eTag == null || eTag.isEmpty()) {
            return eTag;
        }
        // 替换掉前后的双引号
        return eTag.replaceAll("^\"|\"$", "");
    }

}