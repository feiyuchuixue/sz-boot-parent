package com.sz.admin.system.pojo.dto.sysfile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 预签名上传请求DTO
 * @author qcqcqc
 */
@Data
@Schema(description = "预签名上传请求DTO")
public class PreSignedUploadRequestDTO {

    @NotBlank(message = "文件名不能为空")
    @Schema(description = "文件名")
    private String filename;

    @NotBlank(message = "目录标识不能为空")
    @Schema(description = "目录标识")
    private String dirTag;

    @NotNull(message = "文件大小不能为空")
    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String contentType;
}
