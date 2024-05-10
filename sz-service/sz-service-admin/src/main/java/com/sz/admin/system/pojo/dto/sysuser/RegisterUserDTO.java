package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: sz
 * @date: 2023/5/22 8:25
 * @description:
 */
@Data
@Schema(description = "用户注册")
public class RegisterUserDTO {

    @Schema(description = "用户名", name = "username", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "密码", name = "pwd", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pwd;

    @Schema(description = "手机号", example = "15330331111")
    private String phone;

    @Schema(description = "昵称")
    private String nickName;

}
