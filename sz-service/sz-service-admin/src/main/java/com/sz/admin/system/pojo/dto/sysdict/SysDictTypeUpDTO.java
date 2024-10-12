package com.sz.admin.system.pojo.dto.sysdict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "字典类型修改")
public class SysDictTypeUpDTO {

    @NotNull(message = "id不能为")
    @Schema(description = "id")
    private Long id;

    @NotBlank(message = "字典名称不能为空")
    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "账户状态")
    private String typeName;

    @NotBlank(message = "字典码不能为空")
    @Schema(description = "字典码(英文)", requiredMode = Schema.RequiredMode.REQUIRED, example = "account_status")
    private String typeCode;

    @Schema(description = "是否显示", allowableValues = "T,F", example = "T")
    private String isShow;

    @Schema(description = "备注")
    private String remark;

}
