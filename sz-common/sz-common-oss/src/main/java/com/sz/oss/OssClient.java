package com.sz.oss;

import com.sz.core.common.entity.UploadResult;
import com.sz.core.common.enums.CommonResponseEnum;
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
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.Upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * OSS 对象存储客户端
 * <p>
 * 封装 AWS S3 SDK，统一支持 MinIO、阿里云 OSS、腾讯云 COS、七牛云 Kodo。
 * </p>
 *
 * @author sz
 * @since 2024/11/12 14:56
 */
@Service
@RequiredArgsConstructor
public class OssClient {

    private final OssProperties properties;

    private final S3Client s3Client;

    private final S3TransferManager transferManager;

    private final S3Presigner s3Presigner;

    private static final Long DEFAULT_EXPIRE_TIME = 3600L;

    private final S3AsyncClient s3AsyncClient;

    private static final String FILE_SEPARATOR = "/";

    /**
     * 上传文件（含扩展名 / MIME 校验）
     *
     * @param file
     *            文件
     * @param dirTag
     *            目录
     * @param bucket
     *            存储桶，null 或空串时回退到 {@code oss.bucket-name}
     * @return 上传结果
     * @deprecated 请使用 sz-common-resource 的 ResourceService.upload()，
     *             命名策略由场景配置（NamingRuleEnum）控制。
     */
    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    public UploadResult upload(MultipartFile file, String dirTag, String bucket) throws IOException {
        // naming 字段已迁移至 sz-common-resource，此处固定使用 UUID 命名
        String objectName;
        String originalFilename = file.getOriginalFilename();
        objectName = dirTag + generateFileName(originalFilename);
        objectName = objectName.replaceAll("#", "");
        return upload(file.getInputStream(), objectName, file.getSize(), file.getContentType(), originalFilename, bucket);
    }

    /**
     * 上传文件（含扩展名 / MIME 校验，指定完整文件名）
     *
     * @param file
     *            文件
     * @param dirTag
     *            目录标签
     * @param filename
     *            文件名
     * @param bucket
     *            存储桶，null 或空串时回退到 {@code oss.bucket-name}
     * @return 上传结果
     * @deprecated 请使用 sz-common-resource 的 ResourceService.upload()，
     *             命名策略由场景配置（NamingRuleEnum）控制。
     */
    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    public UploadResult upload(MultipartFile file, String dirTag, String filename, String bucket) throws IOException {
        String objectName = dirTag + "/" + filename;
        return upload(file.getInputStream(), objectName, file.getSize(), file.getContentType(), file.getOriginalFilename(), bucket);
    }

    /**
     * 上传文件（含扩展名 / MIME 校验）
     *
     * @param inputStream
     *            输入流
     * @param objectName
     *            对象名（可包含路径）
     * @param size
     *            文件大小
     * @param contentType
     *            MIME 类型
     * @param originalFilename
     *            原始文件名（用于扩展名校验）
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @return 上传结果
     * @deprecated 请使用 sz-common-resource 的 ResourceService.upload()， 校验逻辑已迁移至
     *             resource 模块的安全策略体系。
     */
    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    public UploadResult upload(InputStream inputStream, String objectName, Long size, String contentType, String originalFilename, String bucket) {
        // 扩展名 + MIME 校验：两项均不在白名单时才拦截
        CommonResponseEnum.FILE_UPLOAD_EXT_ERROR.assertFalse(isAllowedExt(originalFilename) && isAllowedMimeType(Objects.requireNonNull(contentType)));
        return uploadDirect(inputStream, objectName, size, contentType, originalFilename, bucket);
    }

    /**
     * 上传文件（跳过扩展名 / MIME 校验）
     * <p>
     * 调用方（如 {@code OssResourceStorageDriver}）已在上层完成校验，此处直接存储。
     * </p>
     *
     * @param inputStream
     *            输入流
     * @param objectName
     *            对象名（可包含路径）
     * @param size
     *            文件大小
     * @param contentType
     *            MIME 类型
     * @param originalFilename
     *            原始文件名（写入元数据）
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @return 上传结果
     */
    public UploadResult uploadDirect(InputStream inputStream, String objectName, Long size, String contentType, String originalFilename, String bucket) {
        String resolvedBucket = resolveBucket(bucket);
        try {
            BlockingInputStreamAsyncRequestBody body = AsyncRequestBody.forBlockingInputStream(size);
            Map<String, String> metaMap = new HashMap<>();
            metaMap.put("original-filename", originalFilename);
            metaMap.put("bucket-name", resolvedBucket);
            Upload upload = transferManager.upload(x -> x.requestBody(body)
                    .putObjectRequest(y -> y.bucket(resolvedBucket).key(objectName).contentType(contentType).metadata(metaMap).build()).build());
            body.writeInputStream(inputStream);
            CompletedUpload uploadResult = upload.completionFuture().join();
            String eTag = normalizeETag(uploadResult.response().eTag());
            String url = getUrl(resolvedBucket) + "/" + objectName;
            return UploadResult.builder().url(url).objectName(objectName).dirTag(getDirTag(objectName)).filename(getFileName(objectName))
                    .contextType(contentType).size(size).eTag(eTag).metaData(metaMap).build();
        } catch (Exception e) {
            CommonResponseEnum.FILE_UPLOAD_ERROR.message("上传文件失败，objectName=[" + objectName + "]，Message:[" + e.getMessage() + "]").assertTrue(true);
            throw new IllegalStateException("unreachable");
        }
    }

    /**
     * 检查文件是否存在（使用全局默认 bucket）
     *
     * @param key
     *            对象名
     * @return 是否存在
     */
    public boolean isFileExists(String key) {
        return isFileExists(null, key);
    }

    /**
     * 检查文件是否存在
     *
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @param key
     *            对象名
     * @return 是否存在
     */
    public boolean isFileExists(String bucket, String key) {
        try {
            s3Client.headObject(b -> b.bucket(resolveBucket(bucket)).key(key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除对象
     *
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @param objectName
     *            对象名
     */
    public void delete(String bucket, String objectName) {
        String resolvedBucket = resolveBucket(bucket);
        s3Client.deleteObject(b -> b.bucket(resolvedBucket).key(objectName));
    }

    /**
     * 生成私有文件临时访问 URL（全局默认 bucket，默认有效期 3600 秒）
     *
     * @param objectName
     *            对象名
     * @return Presigned URL
     * @deprecated 请使用 {@link #getPrivateUrl(String, String, long)}
     */
    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    public String getPrivateUrl(String objectName) {
        return getPrivateUrl(null, objectName, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 生成私有文件临时访问 URL（默认有效期 3600 秒）
     *
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @param objectName
     *            对象名
     * @return Presigned URL
     * @deprecated 请使用 {@link #getPrivateUrl(String, String, long)}
     */
    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    public String getPrivateUrl(String bucket, String objectName) {
        return getPrivateUrl(bucket, objectName, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 生成私有文件临时访问 URL（指定有效期，全局默认 bucket）
     *
     * @param objectName
     *            对象名
     * @param second
     *            有效期（秒）
     * @return Presigned URL
     * @deprecated 请使用 {@link #getPrivateUrl(String, String, long)}
     */
    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    public String getPrivateUrl(String objectName, Long second) {
        return getPrivateUrl(null, objectName, second);
    }

    /**
     * 生成私有文件临时访问 URL（统一入口）
     *
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @param objectName
     *            对象名
     * @param expireSeconds
     *            有效期（秒）
     * @return Presigned URL
     */
    public String getPrivateUrl(String bucket, String objectName, long expireSeconds) {
        String resolvedBucket = resolveBucket(bucket);
        URL url = s3Presigner.presignGetObject(
                x -> x.signatureDuration(Duration.ofSeconds(expireSeconds)).getObjectRequest(y -> y.bucket(resolvedBucket).key(objectName).build()).build())
                .url();
        return url.toString();
    }

    /**
     * 流式下载文件，返回 {@link InputStream}（使用全局默认 bucket）
     *
     * <p>
     * 底层使用 S3 同步客户端的 {@code getObject} 接口，直接返回
     * {@code ResponseInputStream<GetObjectResponse>}，SDK 不会将文件预先缓冲到内存， 适合大文件场景。
     *
     * <p>
     * <b>调用方必须在使用完毕后关闭返回的流</b>，否则 HTTP 连接将无法释放。 推荐使用 try-with-resources：
     * 
     * <pre>
     * try (InputStream in = ossClient.downloadToStream(objectName)) {
     *     // 读取 in ...
     * }
     * </pre>
     *
     * @param objectName
     *            对象名（可包含路径）
     * @return 文件输入流（调用方负责关闭）
     */
    public InputStream downloadToStream(String objectName) {
        return downloadToStream(null, objectName);
    }

    /**
     * 流式下载文件，返回 {@link InputStream}（指定 bucket）
     *
     * <p>
     * 底层使用 S3 同步客户端的 {@code getObject} 接口，直接返回
     * {@code ResponseInputStream<GetObjectResponse>}，SDK 不会将文件预先缓冲到内存， 适合大文件场景。
     *
     * <p>
     * <b>调用方必须在使用完毕后关闭返回的流</b>，否则 HTTP 连接将无法释放。
     *
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @param objectName
     *            对象名（可包含路径）
     * @return 文件输入流（调用方负责关闭）
     */
    public InputStream downloadToStream(String bucket, String objectName) {
        String resolvedBucket = resolveBucket(bucket);
        return s3Client.getObject(GetObjectRequest.builder().bucket(resolvedBucket).key(objectName).build());
    }

    /**
     * 下载文件到输出流（使用全局默认 bucket）
     *
     * @param objectName
     *            对象名
     * @param outputStream
     *            输出流
     */
    public void download(String objectName, OutputStream outputStream) {
        download(null, objectName, outputStream);
    }

    /**
     * 下载文件到输出流
     *
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @param objectName
     *            对象名
     * @param outputStream
     *            输出流
     */
    public void download(String bucket, String objectName, OutputStream outputStream) {
        String resolvedBucket = resolveBucket(bucket);
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(resolvedBucket).key(objectName).build();
            s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).thenAccept(response -> {
                try {
                    outputStream.write(response.asByteArray());
                    outputStream.flush();
                } catch (Exception e) {
                    CommonResponseEnum.FILE_UPLOAD_ERROR.message("写入输出流失败，objectName=[" + objectName + "]").assertTrue(true);
                }
            }).join();
        } catch (Exception e) {
            CommonResponseEnum.FILE_UPLOAD_ERROR
                    .message("下载文件失败，bucket=[" + resolvedBucket + "] objectName=[" + objectName + "]，Message:[" + e.getMessage() + "]").assertTrue(true);
        }
    }

    /**
     * 下载文件到 HTTP 响应（使用全局默认 bucket）
     *
     * @param objectName
     *            对象名
     * @param response
     *            HTTP 响应
     * @param showFileName
     *            下载文件名，空串时使用 objectName 末尾文件名
     */
    public void download(String objectName, HttpServletResponse response, String showFileName) {
        download(null, objectName, response, showFileName);
    }

    /**
     * 下载文件到 HTTP 响应
     *
     * @param bucket
     *            存储桶，null 或空串时回退到全局默认
     * @param objectName
     *            对象名
     * @param response
     *            HTTP 响应
     * @param showFileName
     *            下载文件名，空串时使用 objectName 末尾文件名
     */
    public void download(String bucket, String objectName, HttpServletResponse response, String showFileName) {
        String resolvedBucket = resolveBucket(bucket);
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(resolvedBucket).key(objectName).build();
            String filename = (showFileName == null || showFileName.isEmpty()) ? getFileName(objectName) : showFileName;
            OutputStream os = FileUtils.getOutputStream(response, filename);
            s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).thenAccept(responseBytes -> {
                try {
                    os.write(responseBytes.asByteArray());
                    os.flush();
                } catch (IOException e) {
                    CommonResponseEnum.FILE_UPLOAD_ERROR.message("写入响应失败，objectName=[" + objectName + "]").assertTrue(true);
                }
            }).join();
        } catch (Exception e) {
            CommonResponseEnum.FILE_UPLOAD_ERROR
                    .message("下载文件失败，bucket=[" + resolvedBucket + "] objectName=[" + objectName + "]，Message:[" + e.getMessage() + "]").assertTrue(true);
        }
    }

    /**
     * 获取指定 bucket 的公开访问 URL 前缀（不含末尾 "/"）。
     * <p>
     * MinIO 无 domain：{@code scheme://endpoint/bucketName}<br>
     * MinIO 有 domain：{@code domain/bucketName}<br>
     * 其他厂商无 domain：{@code scheme://bucketName.endpoint}（虚拟主机风格）<br>
     * 其他厂商有 domain：{@code domain}（CDN 域名，已包含 bucket 路由）
     * </p>
     */
    public String getUrl(String bucketName) {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String scheme = properties.getScheme().toString();

        // 规范化 endpoint：去掉末尾斜杠，去掉已有协议前缀（用 scheme 字段替代）
        String cleanEndpoint = stripTrailingSlash(stripScheme(endpoint));

        // 非 MinIO（阿里云 / 腾讯云 / 七牛云）：虚拟主机风格
        if (!properties.resolvePathStyle()) {
            if (StringUtils.isNotEmpty(domain)) {
                return ensureScheme(domain, scheme);
            }
            // 虚拟主机风格：https://bucketName.endpoint
            return scheme + "://" + bucketName + "." + cleanEndpoint;
        }

        // MinIO（路径风格）
        if (StringUtils.isNotEmpty(domain)) {
            // domain 由用户自行配置，直接使用（含 bucket）
            return ensureScheme(domain, scheme).replaceAll("/$", "") + "/" + bucketName;
        }
        // 路径风格：https://endpoint/bucketName
        return scheme + "://" + cleanEndpoint + "/" + bucketName;
    }

    /**
     * 扩展名校验（白名单为空时放行）
     *
     * @param fileName
     *            文件名
     * @return 是否允许
     * @deprecated 校验逻辑已迁移至 sz-common-resource
     *             模块的安全策略体系（ResourceSecurityPolicyProvider）。
     */
    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    public boolean isAllowedExt(String fileName) {
        // 白名单字段已迁移至 sz-common-resource，此处始终放行
        return true;
    }

    /**
     * MIME 类型校验（白名单为空时放行）
     *
     * @param mimeType
     *            MIME 类型
     * @return 是否允许
     * @deprecated 校验逻辑已迁移至 sz-common-resource
     *             模块的安全策略体系（ResourceSecurityPolicyProvider）。
     */
    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    public boolean isAllowedMimeType(String mimeType) {
        // 白名单字段已迁移至 sz-common-resource，此处始终放行
        return true;
    }

    /**
     * 提取文件扩展名（小写，不含点）
     */
    public String getFileExt(String fileName) {
        if (fileName == null) {
            return null;
        }
        int i = fileName.lastIndexOf('.');
        return (i > 0 && i < fileName.length() - 1) ? fileName.substring(i + 1) : null;
    }

    /**
     * 解析最终 bucket：若传入值为 null 或空串，回退到全局配置的 {@code oss.bucket-name}。
     */
    private String resolveBucket(String bucket) {
        if (bucket == null || bucket.isBlank()) {
            return properties.getBucketName();
        }
        return bucket;
    }

    /**
     * 生成 UUID 文件名，如 /20231212/4150e7c6-b92f-419d-b804-0e8be80f5e71.png
     */
    private String generateFileName(String originalFilename) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String localTime = df.format(LocalDate.now());
        String pathStr = FILE_SEPARATOR + localTime + FILE_SEPARATOR;
        String extension = org.springframework.util.StringUtils.getFilenameExtension(originalFilename);
        String fileName = UUID.randomUUID().toString();
        return pathStr + fileName + "." + extension;
    }

    private String getFileName(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        return (lastSlashIndex != -1) ? path.substring(lastSlashIndex + 1) : path;
    }

    private String getDirTag(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int index = str.indexOf('/');
        if (index == 0) {
            index = str.indexOf('/', index + 1);
        }
        if (index == -1) {
            return str;
        }
        return str.substring(0, index);
    }

    private String normalizeETag(String eTag) {
        if (eTag == null || eTag.isEmpty()) {
            return eTag;
        }
        return eTag.replaceAll("(^\")|(\"$)", "");
    }

    /**
     * 若 url 不含协议前缀，则拼接 scheme；若已含协议前缀，直接返回。
     */
    private String ensureScheme(String url, String scheme) {
        if (url.startsWith("https://") || url.startsWith("http://")) {
            return url.replaceAll("/$", ""); // 去掉末尾斜杠
        }
        return scheme + "://" + url.replaceAll("/$", "");
    }

    /**
     * 去掉 endpoint 中的协议前缀（http:// / https://）。 S3Configuration 已使用 scheme 字段拼接，此处只需纯
     * host:port 部分。
     */
    private String stripScheme(String endpoint) {
        if (endpoint == null) {
            return "";
        }
        if (endpoint.startsWith("https://")) {
            return endpoint.substring("https://".length());
        }
        if (endpoint.startsWith("http://")) {
            return endpoint.substring("http://".length());
        }
        return endpoint;
    }

    private String stripTrailingSlash(String s) {
        if (s == null) {
            return "";
        }
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}
