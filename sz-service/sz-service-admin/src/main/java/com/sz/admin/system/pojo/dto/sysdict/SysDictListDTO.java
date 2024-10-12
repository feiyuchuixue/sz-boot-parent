package com.sz.admin.system.pojo.dto.sysdict;

import com.sz.core.common.entity.PageQuery;
import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "列表查询")
public class SysDictListDTO extends PageQuery {

    @NotZero(message = "sysDictTypeId不能为空")
    @Schema(description = "关联sys_dict_type id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Long sysDictTypeId;

    @Schema(description = "字典别名")
    private String alias;

    @Schema(description = "字典名称", example = "禁用")
    private String codeName;

}
