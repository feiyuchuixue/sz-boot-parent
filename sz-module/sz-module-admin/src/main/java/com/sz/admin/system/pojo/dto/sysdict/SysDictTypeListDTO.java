package com.sz.admin.system.pojo.dto.sysdict;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字典类型查询参数")
public class SysDictTypeListDTO extends PageQuery {

    @Schema(description = "字典类型名字")
    private String typeName;

    @Schema(description = "字典类型码")
    private String typeCode;

}
