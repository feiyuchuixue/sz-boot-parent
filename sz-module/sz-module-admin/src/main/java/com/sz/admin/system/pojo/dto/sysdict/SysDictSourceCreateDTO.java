package com.sz.admin.system.pojo.dto.sysdict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "字典来源新增")
public class SysDictSourceCreateDTO {

    @NotBlank(message = "来源编码不能为空")
    private String sourceCode;

    @NotBlank(message = "来源名称不能为空")
    private String sourceName;

    @NotNull(message = "起始ID不能为空")
    private Long startId;

    @NotNull(message = "结束ID不能为空")
    private Long endId;

    @NotBlank(message = "状态不能为空")
    private String status;

    private String remark;
}
