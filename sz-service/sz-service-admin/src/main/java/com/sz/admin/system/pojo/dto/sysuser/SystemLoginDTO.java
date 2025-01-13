package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @since 2022/5/21 19:30
 * 
 */

@Schema(description = "描述信息")
@Data
public class SystemLoginDTO {

    @Schema(description = "用户名", type = "String", name = "username", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "密码", type = "String", name = "password", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
