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
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
 * <b>路径防护</b>：校验 objectKey 和 root 下的规范化路径；上传、读取、删除和存在性检查还校验已存在节点的真实路径边界。 LOCAL
 * 场景的 path 前缀隔离由上层 ResourceService 校验，本驱动不根据 scene 再拼接目录。
 */
@Component
@RequiredArgsConstructor
public class LocalResourceStorageDriver implements ResourceStorageDriver {

    private final ResourceProperties resourceProperties;

    @Override
    public String upload(MultipartFile file, String objectKey, ResourceSceneConfig scene) throws IOException {
        File target = resolveFile(objectKey);
        Path base = resolveBasePath();
        Files.createDirectories(base);
        validateRealPathBoundary(target.toPath(), base, objectKey);
        FileUtils.forceMkdirParent(target);
        validateRealPathBoundary(target.toPath(), base, objectKey);
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
        File file = resolveFileForExistingOperation(objectKey);
        FileUtils.deleteQuietly(file);
    }

    @Override
    public boolean exists(String objectKey, ResourceSceneConfig scene) {
        return resolveFileForExistingOperation(objectKey).exists();
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
        Path base = resolveBasePath();
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
        validateRealPathBoundary(file.toPath(), resolveBasePath(), objectKey);
        return file.toPath().toRealPath().toFile();
    }

    /**
     * 为无需声明受检异常的 exists/delete 操作执行相同的真实路径边界校验。
     */
    private File resolveFileForExistingOperation(String objectKey) {
        File file = resolveFile(objectKey);
        Path base = resolveBasePath();
        if (!Files.exists(base, LinkOption.NOFOLLOW_LINKS)) {
            return file;
        }
        try {
            validateRealPathBoundary(file.toPath(), base, objectKey);
            return file;
        } catch (IOException exception) {
            throw new IllegalArgumentException("无法验证本地文件路径安全性：" + objectKey, exception);
        }
    }

    /**
     * 校验目标自身或最近的已存在父目录解析符号链接后仍位于 root 内。
     *
     * <p>
     * 对尚未创建的上传目标，需要从目标向上查找已存在节点，才能在写入前识别父目录符号链接。
     */
    private void validateRealPathBoundary(Path target, Path base, String objectKey) throws IOException {
        Path realBase = base.toRealPath();
        Path existingPath = target;
        while (existingPath != null && !Files.exists(existingPath, LinkOption.NOFOLLOW_LINKS)) {
            existingPath = existingPath.getParent();
        }
        if (existingPath == null || !existingPath.toRealPath().startsWith(realBase)) {
            throw new IllegalArgumentException("文件路径逃逸（可能存在符号链接攻击）：" + objectKey);
        }
    }

    private Path resolveBasePath() {
        return Paths.get(resourceProperties.getRoot()).toAbsolutePath().normalize();
    }

    /**
     * 校验 objectKey 合法性，委托给 {@link PathSanitizer} 统一处理
     */
    private void validateObjectKey(String objectKey) {
        PathSanitizer.validate(objectKey, PathSanitizer.Mode.OBJECT_KEY);
    }
}
