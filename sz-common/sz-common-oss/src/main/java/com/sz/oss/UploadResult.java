package com.sz.oss;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName UploadResult
 * @Author sz
 * @Date 2024/11/12 16:22
 * @Version 1.0
 */
@Data
@Builder
public class UploadResult {

    // URL
    private String url;

    // 文件名
    private String filename;

    // 对象名称 (带路径)
    private String objectName;

    // 标记
    private String eTag;

    // 标记
    private String dirTag;

    // 文件类型
    private String contextType;

    // 文件大小
    private Long size;

    // 文件id
    private Long fileId;
}
