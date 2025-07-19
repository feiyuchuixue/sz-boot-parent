package com.sz.admin.system.pojo.dto.sysfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 预签名上传响应DTO
 * @author qcqcqc
 */
@Data
@Schema(description = "预签名上传响应DTO")
public class PreSignedUploadResponseDTO {

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "预签名上传URL")
    private String uploadUrl;

    @Schema(description = "对象名称")
    private String objectName;

    @Schema(description = "上传凭证过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "需要的请求头信息")
    private Map<String, String> headers;
}
