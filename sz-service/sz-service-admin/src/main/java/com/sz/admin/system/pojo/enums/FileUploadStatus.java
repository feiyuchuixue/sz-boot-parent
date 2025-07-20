package com.sz.admin.system.pojo.enums;

import lombok.Getter;

/**
 * 文件上传状态枚举
 * @author qcqcqc
 */
@Getter
public enum FileUploadStatus {
    UPLOADING("uploading", "上传中"),
    COMPLETED("completed", "上传完成"),
    FAILED("failed", "上传失败");

    private final String code;
    private final String desc;

    FileUploadStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
