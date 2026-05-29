package com.sz.admin.system.pojo.vo.sysconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "前端Config返回vo")
public class ConfigVO {

    @Schema(description = "参数名")
    private String configName;

    @Schema(description = "参数值")
    private String configValue;

    @Schema(description = "参数Key")
    private String configKey;

}
