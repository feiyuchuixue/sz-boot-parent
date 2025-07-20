package com.sz.admin.system.pojo.dto.sysfile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 确认上传请求DTO
 * @author qcqcqc
 */
@Data
@Schema(description = "确认上传请求DTO")
public class ConfirmUploadRequestDTO {

    @NotNull(message = "文件ID不能为空")
    @Schema(description = "文件ID")
    private Long fileId;
}
