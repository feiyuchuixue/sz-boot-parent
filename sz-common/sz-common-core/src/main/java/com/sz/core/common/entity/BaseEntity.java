package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName BaseEntity
 * @Author sz
 * @Date 2023/12/8 18:28
 * @Version 1.0
 */
@Deprecated
@Data
@Schema(description = "基础Entity通用字段类")
public class BaseEntity {

    @Schema(description = "创建人", example = "1")
    private Integer createId;

    @Schema(description = "更新人", example = "1")
    private Integer updateId;

    @Schema(description = "创建时间", example = "2024-02-01 15:30:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-02-01 15:35:00")
    private LocalDateTime updateTime;

}
