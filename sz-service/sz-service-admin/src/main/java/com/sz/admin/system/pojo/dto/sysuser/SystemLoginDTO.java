package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: sz
 * @date: 2022/5/21 19:30
 * @description:
 */

@Schema(description = "描述信息")
@Data
public class SystemLoginDTO {

    @Schema(description = "用户名", type = "String", name = "username", example = "admin", required = true)
    private String username;

    @Schema(description = "密码", type = "String", name = "password", example = "admin", required = true)
    private String password;

}
