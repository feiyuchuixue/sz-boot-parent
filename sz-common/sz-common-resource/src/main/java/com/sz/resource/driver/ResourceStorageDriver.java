package com.sz.resource.driver;

import com.sz.resource.config.ResourceSceneConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源存储驱动接口
 * <p>
 * 定义资源的上传、读取、删除操作，由具体存储实现类负责执行。
 * </p>
 */
public interface ResourceStorageDriver {

    /**
     * 上传文件
     *
     * @param file
     *            上传的文件
     * @param objectKey
     *            存储键，相对于 root 的完整子路径，如 avatars/1/20260403/abc.png
     * @param scene
     *            场景配置（含存储桶、前缀等信息）
     * @return 存储端返回的 eTag；OSS 驱动非 null，Local 驱动恒为 null
     * @throws IOException
     *             上传失败
     */
    String upload(MultipartFile file, String objectKey, ResourceSceneConfig scene) throws IOException;

    /**
     * 读取文件为字节数组
     *
     * <p>
     * <b>注意：</b>此方法会将文件全量加载进内存，大文件场景请优先使用 {@link #readStream}。
     *
     * @param objectKey
     *            存储键，相对于 root 的完整子路径
     * @param scene
     *            场景配置
     * @return 文件字节数组
     * @throws IOException
     *             读取失败
     */
    byte[] read(String objectKey, ResourceSceneConfig scene) throws IOException;

    /**
     * 读取文件为输入流（流式，适合大文件）
     *
     * <p>
     * 实现类应尽量返回真正的流式 InputStream，避免预先将文件全量缓冲到内存。 <b>调用方负责在使用完毕后关闭返回的流。</b>
     *
     * @param objectKey
     *            存储键，相对于 root 的完整子路径
     * @param scene
     *            场景配置
     * @return 文件输入流（调用方负责关闭）
     * @throws IOException
     *             读取失败
     */
    InputStream readStream(String objectKey, ResourceSceneConfig scene) throws IOException;

    /**
     * 删除文件
     *
     * @param objectKey
     *            存储键，相对于 root 的完整子路径
     * @param scene
     *            场景配置
     */
    void delete(String objectKey, ResourceSceneConfig scene);

    /**
     * 判断文件是否存在
     *
     * @param objectKey
     *            存储键，相对于 root 的完整子路径
     * @param scene
     *            场景配置
     * @return 是否存在
     */
    boolean exists(String objectKey, ResourceSceneConfig scene);

    /**
     * 生成签名 URL（PRESIGNED 场景专用）
     *
     * <p>
     * 仅 OSS 驱动实现；Local 驱动约定抛 {@link UnsupportedOperationException}， 依赖
     * {@code ResourceProperties} 启动校验保证 PRESIGNED ↔ OSS 绑定， 运行时不可达。
     *
     * @param objectKey
     *            存储键，相对于 root 的完整子路径
     * @param scene
     *            场景配置
     * @param expireSeconds
     *            签名有效期（秒）
     * @return 带签名的临时访问 URL
     */
    String presign(String objectKey, ResourceSceneConfig scene, long expireSeconds);
}
