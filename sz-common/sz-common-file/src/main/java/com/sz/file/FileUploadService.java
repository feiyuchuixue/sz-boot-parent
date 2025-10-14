package com.sz.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传服务接口
 * 提供三种不同的文件上传方式：
 * 1. 基础上传 - 使用默认配置和路径
 * 2. 指定路径上传 - 允许自定义文件存储路径
 * 3. 完整参数上传 - 可控制是否覆盖同名文件
 *
 * @author sz
 * @since 2025-10-13
 */
public interface FileUploadService {

    /**
     * 基础上传方法
     * 使用默认路径和配置进行文件上传
     *
     * @param file 待上传的文件，不能为空
     * @return UploadResult 上传结果，包含文件访问URL、文件名等信息
     * @throws IOException IO异常，如文件读写错误
     * @throws IllegalArgumentException 参数异常，如文件为空
     */
    UploadResult upload(MultipartFile file) throws IOException;

    /**
     * 指定路径上传方法
     * 允许自定义文件存储路径
     *
     * @param file 待上传的文件，不能为空
     * @param path 文件存储路径，如 "images/avatar/"，如果路径不存在会自动创建
     * @return UploadResult 上传结果，包含文件访问URL、文件名等信息
     * @throws IOException IO异常，如文件读写错误
     * @throws IllegalArgumentException 参数异常，如文件为空
     */
    UploadResult upload(MultipartFile file, String path) throws IOException;

    /**
     * 完整参数上传方法
     * 支持自定义路径和是否覆盖同名文件
     *
     * @param file 待上传的文件，不能为空
     * @param path 文件存储路径，如 "images/avatar/"，如果路径不存在会自动创建
     * @param overwrite 是否覆盖同名文件，true表示覆盖，false表示生成新文件名
     * @return UploadResult 上传结果，包含文件访问URL、文件名等信息
     * @throws IOException IO异常，如文件读写错误
     * @throws IllegalArgumentException 参数异常，如文件为空
     */
    UploadResult upload(MultipartFile file, String path, boolean overwrite) throws IOException;
}