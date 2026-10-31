package com.sz.resource.driver;

import com.sz.core.common.entity.UploadResult;
import com.sz.oss.OssClient;
import com.sz.oss.OssClientRegistry;
import com.sz.oss.OssSceneConnection;
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
 * 通过 {@link OssClientRegistry} 选择客户端，将资源存储委托给 {@link OssClient}（sz-common-oss
 * 提供）。 底层厂商、连接和寻址风格由 sz.oss 默认配置及场景覆盖决定，本驱动不做厂商差异化处理。 逻辑 objectKey 含 bucket
 * 前缀，调用 SDK 时剥离该前缀，另行传入生效 bucket。
 * </p>
 *
 * <p>
 * 只有在 Spring 容器中存在 {@link OssClientRegistry} Bean 时，本驱动才会被注册。 若项目未引入
 * sz-common-oss 或未完成 OSS 配置（未产生 Bean），本类不生效。
 * </p>
 */
@Component
@ConditionalOnBean(OssClientRegistry.class)
@RequiredArgsConstructor
public class OssResourceStorageDriver implements ResourceStorageDriver {

    private final OssClientRegistry ossClientRegistry;

    @Override
    public String upload(MultipartFile file, String objectKey, ResourceSceneConfig scene) throws IOException {
        String bucket = resolveBucket(scene);
        String physicalKey = ObjectKeyUtils.toPhysicalKey(objectKey, bucket);
        // 上层 ResourceService 已完成扩展名/MIME/大小校验，直接调用 uploadDirect 跳过重复校验
        UploadResult raw = resolveClient(scene).uploadDirect(file.getInputStream(), physicalKey, file.getSize(), file.getContentType(),
                file.getOriginalFilename(), bucket);
        return raw.getETag();
    }

    @Override
    public byte[] read(String objectKey, ResourceSceneConfig scene) throws IOException {
        // OssClient.download 底层使用 AsyncResponseTransformer.toBytes()，会将文件全量加载进内存。
        // 仅在确实需要 byte[] 时使用此方法；大文件场景请优先使用 readStream()。
        String bucket = resolveBucket(scene);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resolveClient(scene).download(bucket, ObjectKeyUtils.toPhysicalKey(objectKey, bucket), baos);
        return baos.toByteArray();
    }

    @Override
    public InputStream readStream(String objectKey, ResourceSceneConfig scene) throws IOException {
        // 使用 OssClient.downloadToStream()：底层调用 S3Client.getObject()，
        // 返回真正的流式 InputStream，SDK 不会预先将文件缓冲到内存，适合大文件场景。
        // 调用方负责关闭返回的流。
        String bucket = resolveBucket(scene);
        return resolveClient(scene).downloadToStream(bucket, ObjectKeyUtils.toPhysicalKey(objectKey, bucket));
    }

    @Override
    public void delete(String objectKey, ResourceSceneConfig scene) {
        String bucket = resolveBucket(scene);
        resolveClient(scene).delete(bucket, ObjectKeyUtils.toPhysicalKey(objectKey, bucket));
    }

    @Override
    public boolean exists(String objectKey, ResourceSceneConfig scene) {
        String bucket = resolveBucket(scene);
        return resolveClient(scene).isFileExists(bucket, ObjectKeyUtils.toPhysicalKey(objectKey, bucket));
    }

    @Override
    public String presign(String objectKey, ResourceSceneConfig scene, long expireSeconds) {
        String bucket = resolveBucket(scene);
        String physicalKey = ObjectKeyUtils.toPhysicalKey(objectKey, bucket);
        return resolveClient(scene).getPrivateUrl(bucket, physicalKey, expireSeconds);
    }

    /**
     * 解析存储桶：上层静态场景校验或动态场景解析已将场景级 bucket 或全局默认值填入生效配置。
     */
    private String resolveBucket(ResourceSceneConfig scene) {
        return scene.getBucket();
    }

    private OssClient resolveClient(ResourceSceneConfig scene) {
        OssSceneConnection connection = scene.toOssSceneConnection();
        return ossClientRegistry.resolve(scene.getCode(), connection, scene.getAddressingStyle());
    }

}
