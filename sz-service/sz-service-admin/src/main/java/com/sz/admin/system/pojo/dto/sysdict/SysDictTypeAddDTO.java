package com.sz.admin.system.pojo.dto.sysdict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "字典类型添加")
public class SysDictTypeAddDTO {

    @NotBlank(message = "字典名称不能为空")
    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "账户状态")
    private String typeName;

    @NotBlank(message = "字典码不能为空")
    @Schema(description = "字典码(英文)", requiredMode = Schema.RequiredMode.REQUIRED, example = "account_status")
    private String typeCode;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "字典类型")
    private String type;

}
