package com.sz.resource.driver;

import com.sz.resource.config.ResourceProperties;
import com.sz.resource.config.ResourceSceneConfig;
import com.sz.resource.util.PathSanitizer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地磁盘存储驱动
 *
 * <p>
 * 将文件存储于服务器本地磁盘。物理路径由全局 {@code root} 和 {@code objectKey} 共同决定：
 * 
 * <pre>
 * 物理路径 = root + "/" + objectKey
 * </pre>
 *
 * <p>
 * 示例：
 * 
 * <pre>
 *   root      = ./data
 *   objectKey = avatars/1/20260403/abc.png
 *   物理路径  = ./data/avatars/1/20260403/abc.png
 * </pre>
 *
 * <p>
 * <b>路径穿越防护</b>：objectKey 不允许包含 ".."，且最终路径必须在 root 目录之下。
 */
@Component
@RequiredArgsConstructor
public class LocalResourceStorageDriver implements ResourceStorageDriver {

    private final ResourceProperties resourceProperties;

    @Override
    public String upload(MultipartFile file, String objectKey, ResourceSceneConfig scene) throws IOException {
        File target = resolveFile(objectKey);
        FileUtils.forceMkdirParent(target);
        file.transferTo(target);
        // LOCAL 无 eTag 概念，返回 null
        return null;
    }

    @Override
    public byte[] read(String objectKey, ResourceSceneConfig scene) throws IOException {
        File file = resolveFileForRead(objectKey);
        return FileUtils.readFileToByteArray(file);
    }

    @Override
    public InputStream readStream(String objectKey, ResourceSceneConfig scene) throws IOException {
        File file = resolveFileForRead(objectKey);
        return new FileInputStream(file);
    }

    @Override
    public void delete(String objectKey, ResourceSceneConfig scene) {
        File file = resolveFile(objectKey);
        FileUtils.deleteQuietly(file);
    }

    @Override
    public boolean exists(String objectKey, ResourceSceneConfig scene) {
        return resolveFile(objectKey).exists();
    }

    @Override
    public String presign(String objectKey, ResourceSceneConfig scene, long expireSeconds) {
        // LOCAL 存储无签名概念；ResourceProperties 启动校验已保证 PRESIGNED ↔ OSS 绑定，运行时不应到达此处
        throw new UnsupportedOperationException("LOCAL 存储不支持签名 URL，场景：" + scene.getCode());
    }

    /**
     * 将 objectKey 解析为本地文件的绝对路径
     *
     * <p>
     * 物理路径 = root + "/" + objectKey，objectKey 中包含完整的相对子路径（含子目录和文件名）。
     * 例如：root=./data, objectKey=avatars/1/20260403/abc.png →
     * 物理路径：{workdir}/data/avatars/1/20260403/abc.png
     *
     * @param objectKey
     *            存储键（相对于 root 的完整路径，如 avatars/1/20260403/abc.png）
     * @return 本地 File 对象
     * @throws IllegalArgumentException
     *             objectKey 包含非法字符或路径穿越
     */
    private File resolveFile(String objectKey) {
        validateObjectKey(objectKey);
        String root = resourceProperties.getRoot();
        Path base = Paths.get(root).toAbsolutePath().normalize();
        // objectKey 直接拼接在 root 下，保留完整子目录结构
        Path target = base.resolve(objectKey).normalize();
        // 安全校验：目标路径必须在 root 目录之下，防止路径穿越
        if (!target.startsWith(base)) {
            throw new IllegalArgumentException("非法文件路径，疑似路径穿越攻击：" + objectKey);
        }
        return target.toFile();
    }

    /**
     * 读取场景下的文件解析：在 {@link #resolveFile(String)} 基础上增加符号链接防护。
     *
     * <p>
     * 通过 {@link Path#toRealPath()} 解析符号链接后的真实路径， 再次校验真实路径是否仍在 root 目录下，防止通过符号链接逃逸到
     * root 外部。
     *
     * @param objectKey
     *            存储键
     * @return 本地 File 对象（已验证存在且在 root 内）
     * @throws FileNotFoundException
     *             文件不存在
     * @throws IllegalArgumentException
     *             符号链接指向 root 外部
     */
    private File resolveFileForRead(String objectKey) throws IOException {
        File file = resolveFile(objectKey);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在：" + objectKey);
        }
        // 符号链接防护：解析真实路径后再次校验是否在 root 目录下
        Path realPath = file.toPath().toRealPath();
        Path base = Paths.get(resourceProperties.getRoot()).toAbsolutePath().normalize().toRealPath();
        if (!realPath.startsWith(base)) {
            throw new IllegalArgumentException("文件路径逃逸（可能存在符号链接攻击）：" + objectKey);
        }
        return realPath.toFile();
    }

    /**
     * 校验 objectKey 合法性，委托给 {@link PathSanitizer} 统一处理
     */
    private void validateObjectKey(String objectKey) {
        PathSanitizer.validate(objectKey, PathSanitizer.Mode.OBJECT_KEY);
    }
}
