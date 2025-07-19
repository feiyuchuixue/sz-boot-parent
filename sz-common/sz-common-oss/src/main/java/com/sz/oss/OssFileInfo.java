package com.sz.oss;

import lombok.Data;

/**
 * OSS文件信息
 * @author qcqcqc
 */
@Data
public class OssFileInfo {

    private String url;
    private Long size;
    private String eTag;
    private String contentType;

}
