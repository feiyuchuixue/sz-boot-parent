package com.sz.admin.system.pojo.vo.sysresource;

import com.sz.resource.enums.ServeModeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源列表返回 VO
 *
 * @author sz-admin
 */
@Data
@Schema(description = "资源列表返回VO")
public class SysResourceVO {

    @Schema(description = "主键（雪花ID）")
    private Long id;

    @Schema(description = "场景编码，如 sso.provider.logo")
    private String sceneCode;

    @Schema(description = "用途名称，未配置名称时为场景编码")
    private String sceneName;

    @Schema(description = "当前场景访问模式，场景配置缺失时为空")
    private ServeModeEnum serveMode;

    @Schema(description = "存储键（相对路径/objectName）")
    private String objectKey;

    @Schema(description = "原始文件名")
    private String originName;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "MIME 类型")
    private String contentType;

    @Schema(description = "存储类型：LOCAL / OSS")
    private String storageType;

    @Schema(description = "文件 ETag")
    private String eTag;

    @Schema(description = "命名用业务标识，不代表关联业务记录ID")
    private String bizKey;

    @Schema(description = "完整访问地址（查询时动态生成）")
    private String accessUrl;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "资源登记时间")
    private LocalDateTime createTime;

}
