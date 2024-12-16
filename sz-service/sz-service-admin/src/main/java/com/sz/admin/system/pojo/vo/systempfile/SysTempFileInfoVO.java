package com.sz.admin.system.pojo.vo.systempfile;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysTempFileInfoVO {

    @Schema(description = "")
    private Long id;

    @Schema(description = "文件ID")
    private Integer sysFileId;

    @Schema(description = "模版名")
    private String tempName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "目录标识")
    private String dirTag;

    @Schema(description = "文件大小")
    private Long size;

    @Schema(description = "文件域名")
    private String url;

    @Schema(description = "对象名（唯一）")
    private String objectName;

    @Schema(description = "文件类型")
    private String contextType;

    @JsonProperty(value = "eTag")
    @Schema(description = "eTag标识")
    private String eTag;

}
