package com.sz.resource.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源上传结果
 */
@Data
@Builder
@Schema(description = "资源上传结果")
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUploadResult {

    /**
     * 存储键（objectKey），即数据库应存储的值。 格式为相对于全局 root 的完整子路径，不含 root 前缀。
     * 示例：avatars/1/20260403/abc.png
     */
    @Schema(description = "存储键（应存入数据库的值）")
    private String objectKey;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String originName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）")
    private Long size;

    /**
     * MIME 类型
     */
    @Schema(description = "MIME 类型")
    private String contentType;

    /**
     * 完整可访问 URL（PUBLIC 场景直接返回；PRIVATE 场景返回临时 URL）
     */
    @Schema(description = "完整可访问 URL")
    private String accessUrl;

    /**
     * 文件 ETag（MD5 或存储端返回的标识）
     */
    @Schema(description = "文件 ETag")
    private String eTag;

    /**
     * sys_resource 记录 ID
     */
    @Schema(description = "sys_resource 记录 ID")
    private Long resourceId;
}
