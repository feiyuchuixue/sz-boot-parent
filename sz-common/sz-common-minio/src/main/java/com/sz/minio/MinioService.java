package com.sz.minio;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

@Service
public class MinioService {

    @Resource
    private MinioClient minioClient;

    @Resource
    private String bucketName;

    /**
     * 判断存储桶是否存在
     *
     * @return boolean
     */
    @SneakyThrows
    public boolean isBucketExists() {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建存储桶
     */
    @SneakyThrows
    public void createBucket() {
        if (!isBucketExists()) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 判断目录是否存在
     *
     * @param dirName 目录名称
     * @return boolean
     */
    @SneakyThrows
    public boolean isDirExists(String dirName) {
        boolean exists = false;

        Iterable<Result<Item>> objects = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(dirName)
                .recursive(false)
                .build()
        );

        for (Result<Item> object : objects) {
            Item item = object.get();
            System.out.println("item.objectName() = " + item.objectName());
            if (item.isDir() && item.objectName().replace("/", "").equals(dirName)) {
                exists = true;
            }
        }

        return exists;
    }

    public boolean isObjectExists(String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取文件流
     *
     * @param objectName 文件名
     * @return 二进制流
     */
    @SneakyThrows
    public InputStream getObject(String objectName) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * 断点下载
     *
     * @param objectName 文件名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度
     * @return 二进制流
     */
    @SneakyThrows
    public InputStream getObject(String objectName, long offset, long length) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .offset(offset)
                        .length(length)
                        .build());
    }

    /**
     * 使用MultipartFile进行文件上传
     *
     * @param file        文件名
     * @param objectName  对象名
     * @param contentType 类型
     * @return ObjectWriteResponse
     */
    @SneakyThrows
    public ObjectWriteResponse uploadFile(MultipartFile file, String objectName, String contentType) {
        InputStream inputStream = file.getInputStream();
        try {
            return minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(contentType)
                            .stream(inputStream, inputStream.available(), -1)
                            .build());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 上传本地文件
     *
     * @param objectName 对象名称
     * @param fileName   本地文件路径
     * @return ObjectWriteResponse
     */
    @SneakyThrows
    public ObjectWriteResponse uploadFile(String objectName, String fileName) {
        return minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(fileName)
                        .build());
    }

    /**
     * 通过流上传文件
     *
     * @param objectName  文件对象
     * @param inputStream 文件流
     * @return ObjectWriteResponse
     */
    @SneakyThrows
    public ObjectWriteResponse uploadFile(String objectName, InputStream inputStream) {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
    }

    /**
     * 创建文件夹或目录
     *
     * @param objectName 目录路径
     * @return ObjectWriteResponse
     */
    @SneakyThrows
    public ObjectWriteResponse createDir(String objectName) {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }

    /**
     * 获取文件信息, 如果抛出异常则说明文件不存在
     *
     * @param objectName 文件名称
     * @return String
     */
    @SneakyThrows
    public String getFileStatusInfo(String objectName) {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()).toString();
    }

    /**
     * 拷贝文件
     *
     * @param objectName    文件名
     * @param srcBucketName 目标存储桶
     * @param srcObjectName 目标文件名
     */
    @SneakyThrows
    public ObjectWriteResponse copyFile(String objectName, String srcBucketName, String srcObjectName) {
        return minioClient.copyObject(
                CopyObjectArgs.builder()
                        .source(CopySource.builder().bucket(bucketName).object(objectName).build())
                        .bucket(srcBucketName)
                        .object(srcObjectName)
                        .build());
    }

    /**
     * 删除文件
     *
     * @param objectName 文件名称
     */
    @SneakyThrows
    public void removeFile(String objectName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * 获取文件外链
     *
     * @param objectName 文件名
     * @param expires    过期时间 <=7 秒 （外链有效时间（单位：秒））
     * @return url
     */
    @SneakyThrows
    public String getPreSignedObjectUrl(String objectName, Integer expires) {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .expiry(expires)
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build();
        return minioClient.getPresignedObjectUrl(args);
    }

    /**
     * 获得文件外链
     *
     * @param objectName 对象名称
     * @return url
     */
    @SneakyThrows
    public String getPublicObjectUrl(String objectName) {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build();

        URI uri = new URI(minioClient.getPresignedObjectUrl(args));
        return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null).toString();
    }

}
