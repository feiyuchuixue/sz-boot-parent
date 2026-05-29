package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SysUserPasswordDTO
 *
 * @author sz
 * @since 2023/8/30
 */
@Data
public class SysUserPasswordDTO {

    @Schema(description = "原始密码")
    private String oldPwd;

    @Schema(description = "新密码")
    private String newPwd;
}
