package com.sz.resource.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 多文件 JSON 列的标准入库结构。
 *
 * @author sz
 */
@Data
@Schema(description = "文件资源引用（多文件 JSON 列入库结构）")
public class ResourceRef {

    @Schema(description = "存储键（objectKey），入库的唯一稳定标识，如 teacher/1/20260403/report.pdf")
    private String objectKey;

    @Schema(description = "原始文件名，用于前端展示")
    private String originName;

    @Schema(description = "MIME 类型，用于前端判断文件类型（如 image/png）")
    private String contentType;

    @Schema(description = "场景编码，入库，查询时 fillAccessUrl 依赖此字段调 resolveUrl")
    private String sceneCode;

    @Schema(description = "资源id")
    private Long resourceId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "可访问 URL，由查询时 resolveUrl 动态填充，不入库")
    private String accessUrl;

}
