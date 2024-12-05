package com.sz.admin.system.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * SysTempFile修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Schema(description = "SysTempFile修改DTO")
public class SysTempFileUpdateDTO {

    @Schema(description =  "")
    private Long id;

    @Schema(description =  "文件ID")
    private Integer sysFileId;

    @Schema(description =  "模版名")
    private String tempName;

    @Schema(description =  "地址")
    private String url;

    @Schema(description =  "备注")
    private String remark;

}