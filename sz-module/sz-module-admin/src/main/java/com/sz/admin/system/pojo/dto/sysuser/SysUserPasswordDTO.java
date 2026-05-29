package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * SysUserPasswordDTO
 *
 * @author sz
 * @since 2023/8/30
 */
@Data
public class SysUserPasswordDTO {

    @NotBlank(message = "原始密码不能为空")
    @Schema(description = "原始密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPwd;

    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,16}$", message = "新密码须包含大小写字母及数字且长度为8到16位")
    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPwd;
}
