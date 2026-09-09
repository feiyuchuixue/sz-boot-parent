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

    @Schema(description = "存储键（objectKey），由后端按 resourceId 查询资源元数据后重建")
    private String objectKey;

    @Schema(description = "原始文件名，用于前端展示")
    private String originName;

    @Schema(description = "MIME 类型，用于前端判断文件类型（如 image/png）")
    private String contentType;

    @Schema(description = "场景编码，用于资源来源校验和存储定位")
    private String sceneCode;

    @Schema(description = "资源主标识；受保护业务必填，网络 JSON 对超出安全范围的 Long 使用字符串")
    private Long resourceId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "可访问 URL，仅用于 DIRECT/PRESIGNED 动态回显，不作为授权或持久定位依据")
    private String accessUrl;

}
