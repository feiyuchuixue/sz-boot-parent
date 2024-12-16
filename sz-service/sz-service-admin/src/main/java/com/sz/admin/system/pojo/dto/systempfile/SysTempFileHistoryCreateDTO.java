package com.sz.admin.system.pojo.dto.systempfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysTempFile添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Schema(description = "SysTempFileHistory添加DTO")
public class SysTempFileHistoryCreateDTO {

    @Schema(description = "模板文件ID")
    private Long sysTempFileId;

    @Schema(description = "文件ID")
    private Long sysFileId;

    @Schema(description = "模版名")
    private String tempName;

    @Schema(description = "地址")
    private String url;

    @Schema(description = "备注")
    private String remark;

}