package com.sz.oss;

import com.sz.core.common.entity.UploadResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
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
import java.util.HashMap;
import java.util.Map;

/**
 * OSS 对象存储客户端
 * <p>
 * 封装 AWS S3 SDK，统一支持 MinIO、RustFS、阿里云 OSS、腾讯云 COS、七牛云 Kodo。
 * </p>
 *
 * @author sz
 * @since 2024/11/12 14:56
 */
@Service
@ConditionalOnProperty(prefix = "sz.oss", name = "endpoint")
@Primary
public class OssClient {

    private final OssProperties properties;

    private final OssSceneConnection sceneConnection;

    private final OssAddressingStyle connectionAddressingStyle;

    private final OssProperties globalDefaults;

    private final S3Client s3Client;

    private final S3TransferManager transferManager;

    private final S3Presigner s3Presigner;

    private final S3AsyncClient s3AsyncClient;

    @Autowired
    public OssClient(OssProperties properties, S3Client s3Client, S3TransferManager transferManager, S3Presigner s3Presigner, S3AsyncClient s3AsyncClient) {
        this.properties = properties;
        this.sceneConnection = null;
        this.connectionAddressingStyle = null;
        this.globalDefaults = null;
        this.s3Client = s3Client;
        this.transferManager = transferManager;
        this.s3Presigner = s3Presigner;
        this.s3AsyncClient = s3AsyncClient;
    }

    OssClient(OssSceneConnection sceneConnection, S3Client s3Client, S3TransferManager transferManager, S3Presigner s3Presigner, S3AsyncClient s3AsyncClient) {
        this(sceneConnection, new OssProperties(), null, s3Client, transferManager, s3Presigner, s3AsyncClient);
    }

    OssClient(OssSceneConnection sceneConnection, OssProperties globalDefaults, OssAddressingStyle connectionAddressingStyle, S3Client s3Client,
            S3TransferManager transferManager, S3Presigner s3Presigner, S3AsyncClient s3AsyncClient) {
        this.properties = null;
        this.sceneConnection = sceneConnection;
        this.connectionAddressingStyle = connectionAddressingStyle;
        this.globalDefaults = globalDefaults;
        this.s3Client = s3Client;
        this.transferManager = transferManager;
        this.s3Presigner = s3Presigner;
        this.s3AsyncClient = s3AsyncClient;
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
     * 路径风格会在公开访问根地址后追加 bucket；虚拟主机风格使用已指向 bucket 的 domain， 或在未配置 domain 时将 bucket
     * 作为 endpoint 的子域名。场景独立连接不使用全局 domain， 按自身 endpoint 和生效寻址风格推导。
     * </p>
     */
    public String getUrl(String bucketName) {
        return sceneConnection == null
                ? properties.resolvePublicUrl(bucketName)
                : sceneConnection.resolvePublicUrl(bucketName, connectionAddressingStyle, globalDefaults);
    }

    /**
     * 提取文件扩展名（保持原始大小写，不含点）
     */
    public String getFileExt(String fileName) {
        if (fileName == null) {
            return null;
        }
        int i = fileName.lastIndexOf('.');
        return (i > 0 && i < fileName.length() - 1) ? fileName.substring(i + 1) : null;
    }

    /**
     * 解析最终 bucket：若传入值为 null 或空串，回退到全局配置的 {@code sz.oss.bucketName}。
     */
    private String resolveBucket(String bucket) {
        return sceneConnection == null ? properties.resolveBucketName(bucket) : globalDefaults.resolveBucketName(bucket);
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

}
