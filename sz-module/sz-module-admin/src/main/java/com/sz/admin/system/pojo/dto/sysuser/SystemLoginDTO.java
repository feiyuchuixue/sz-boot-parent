package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author sz
 * @since 2022/5/21 19:30
 *
 */

@Schema(description = "描述信息")
@Data
public class SystemLoginDTO {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", type = "String", name = "username", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", type = "String", name = "password", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
