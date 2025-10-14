package com.sz.file;

import lombok.Data;

/**
 * 文件上传结果类
 * 封装文件上传后的相关信息
 *
 * @author sz
 * @since 2025-10-13
 */
@Data
public class UploadResult {

    /**
     * 文件访问URL
     * 可用于直接访问文件的完整URL地址
     */
    private String url;

    /**
     * 文件名
     * 上传后生成的唯一文件名
     */
    private String filename;

    /**
     * 文件存储路径
     * 文件在服务器上的完整存储路径
     */
    private String path;

    /**
     * 文件大小（字节）
     * 上传文件的大小，单位为字节
     */
    private Long size;

    /**
     * 文件类型（MIME类型）
     * 如: image/png, text/plain 等
     */
    private String contentType;

    /**
     * 原始文件名
     * 用户上传时的原始文件名
     */
    private String originalFilename;

    /**
     * 文件扩展名
     * 如: .jpg, .png, .doc 等
     */
    private String extension;
}