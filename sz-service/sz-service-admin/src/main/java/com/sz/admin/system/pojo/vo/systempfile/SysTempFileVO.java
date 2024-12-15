package com.sz.admin.system.pojo.vo.systempfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * <p>
 * SysTempFile返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Schema(description = "SysTempFile返回vo")
public class SysTempFileVO {

    @Schema(description = "")
    private Long id;

    @Schema(description = "文件ID")
    private Integer sysFileId;

    @Schema(description = "模版名")
    private String tempName;

    @Schema(description = "地址")
    private String url;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "逻辑删除")
    private String delFlag;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private Long updateId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}