package com.sz.admin.system.pojo.dto.sysdict;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字典来源查询参数")
public class SysDictSourceListDTO extends PageQuery {

    @Schema(description = "来源编码")
    private String sourceCode;

    @Schema(description = "来源名称")
    private String sourceName;
}
