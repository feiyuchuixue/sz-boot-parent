package com.sz.admin.system.pojo.dto.sysconfig;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "列表查询")
public class SysConfigListDTO extends PageQuery {

    @Schema(description = "参数名", example = "test")
    private String configName;

    @Schema(description = "参数Key", example = "key1")
    private String configKey;

}
