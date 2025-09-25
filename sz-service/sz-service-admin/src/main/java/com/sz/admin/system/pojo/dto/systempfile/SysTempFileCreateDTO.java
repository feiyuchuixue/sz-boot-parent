package com.sz.admin.system.pojo.dto.systempfile;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import com.sz.oss.UploadResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SysTempFile添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Schema(description = "SysTempFile添加DTO")
public class SysTempFileCreateDTO {

    @Schema(description = "文件ID")
    private Long sysFileId;

    @Schema(description = "模版名")
    private String tempName;

    @Schema(description = "地址")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<UploadResult> url;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "标识")
    private String alias;

}