package com.sz.resource.driver;

import com.sz.core.common.entity.UploadResult;
import com.sz.oss.OssClient;
import com.sz.resource.config.ResourceSceneConfig;
import com.sz.resource.util.ObjectKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
/**
 * OSS 对象存储驱动
 * <p>
 * 将资源存储委托给 {@link OssClient}（sz-common-oss 提供）。 具体底层厂商（MinIO / 阿里云 OSS / 七牛云 /
 * 腾讯云 COS 等） 由 oss.* 配置决定，本驱动完全透明，不做厂商差异化处理。
 * </p>
 *
 * <p>
 * 只有在 Spring 容器中存在 {@link OssClient} Bean 时，本驱动才会被注册。 若项目未引入 sz-common-oss 或未完成
 * OSS 配置（未产生 Bean），本类不生效。
 * </p>
 */
@Component
@ConditionalOnBean(OssClient.class)
@RequiredArgsConstructor
public class OssResourceStorageDriver implements ResourceStorageDriver {

    private final OssClient ossClient;

    @Override
    public String upload(MultipartFile file, String objectKey, ResourceSceneConfig scene) throws IOException {
        String bucket = resolveBucket(scene);
        String physicalKey = ObjectKeyUtils.toPhysicalKey(objectKey, bucket);
        // 上层 ResourceService 已完成扩展名/MIME/大小校验，直接调用 uploadDirect 跳过重复校验
        UploadResult raw = ossClient.uploadDirect(file.getInputStream(), physicalKey, file.getSize(), file.getContentType(), file.getOriginalFilename(),
                bucket);
        return raw.getETag();
    }

    @Override
    public byte[] read(String objectKey, ResourceSceneConfig scene) throws IOException {
        // OssClient.download 底层使用 AsyncResponseTransformer.toBytes()，会将文件全量加载进内存。
        // 仅在确实需要 byte[] 时使用此方法；大文件场景请优先使用 readStream()。
        String bucket = resolveBucket(scene);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ossClient.download(bucket, ObjectKeyUtils.toPhysicalKey(objectKey, bucket), baos);
        return baos.toByteArray();
    }

    @Override
    public InputStream readStream(String objectKey, ResourceSceneConfig scene) throws IOException {
        // 使用 OssClient.downloadToStream()：底层调用 S3Client.getObject()，
        // 返回真正的流式 InputStream，SDK 不会预先将文件缓冲到内存，适合大文件场景。
        // 调用方负责关闭返回的流。
        String bucket = resolveBucket(scene);
        return ossClient.downloadToStream(bucket, ObjectKeyUtils.toPhysicalKey(objectKey, bucket));
    }

    @Override
    public void delete(String objectKey, ResourceSceneConfig scene) {
        String bucket = resolveBucket(scene);
        ossClient.delete(bucket, ObjectKeyUtils.toPhysicalKey(objectKey, bucket));
    }

    @Override
    public boolean exists(String objectKey, ResourceSceneConfig scene) {
        String bucket = resolveBucket(scene);
        return ossClient.isFileExists(bucket, ObjectKeyUtils.toPhysicalKey(objectKey, bucket));
    }

    @Override
    public String presign(String objectKey, ResourceSceneConfig scene, long expireSeconds) {
        String bucket = resolveBucket(scene);
        String physicalKey = ObjectKeyUtils.toPhysicalKey(objectKey, bucket);
        return ossClient.getPrivateUrl(bucket, physicalKey, expireSeconds);
    }

    /**
     * 解析存储桶：场景级 bucket 为 OSS 场景必填项（已在 ResourceProperties 启动校验），直接返回
     */
    private String resolveBucket(ResourceSceneConfig scene) {
        return scene.getBucket();
    }

}
