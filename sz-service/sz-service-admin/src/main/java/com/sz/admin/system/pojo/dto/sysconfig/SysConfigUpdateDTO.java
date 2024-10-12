package com.sz.admin.system.pojo.dto.sysconfig;

import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "参数修改")
public class SysConfigUpdateDTO {

    @NotZero
    @Schema(description = "参数id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "参数名")
    private String configName;

    @Schema(description = "key")
    private String configKey;

    @Schema(description = "value")
    private String configValue;

    @Schema(description = "备注")
    private String remark;

}
