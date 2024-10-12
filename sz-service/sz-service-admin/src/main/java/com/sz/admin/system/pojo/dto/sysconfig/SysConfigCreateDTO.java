package com.sz.admin.system.pojo.dto.sysconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "参数添加")
public class SysConfigCreateDTO {

    @Schema(description = "参数名")
    private String configName;

    @Schema(description = "key")
    private String configKey;

    @Schema(description = "value")
    private String configValue;

    @Schema(description = "备注")
    private String remark;

}
