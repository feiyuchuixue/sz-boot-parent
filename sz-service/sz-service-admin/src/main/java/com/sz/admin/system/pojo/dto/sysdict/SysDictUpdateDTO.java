package com.sz.admin.system.pojo.dto.sysdict;

import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "字典修改")
public class SysDictUpdateDTO {

    @NotNull(message = "id不能为空")
    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Long id;

    @NotNull(message = "sysDictTypeId不能为空")
    @Schema(description = "关联sys_dict_type id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Long sysDictTypeId;

    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "禁用")
    private String codeName;

    @Schema(description = "字典别名")
    private String alias;

    @Schema(description = "排序")
    @NotZero(message = "排序不能为空或0")
    @Min(1)
    @Max(999999)
    private Integer sort;

    @Schema(description = "回显样式")
    private String callbackShowStyle;

    @Schema(description = "备注")
    private String remark;

}
