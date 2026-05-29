package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 联系方式解绑DTO（手机号 / 邮箱）
 *
 * @author sz
 * @since 2026-04-30
 */
@Data
@Schema(description = "联系方式解绑DTO")
public class SysUserContactUnbindDTO {

    @NotBlank(message = "field不能为空")
    @Pattern(regexp = "^(phone|email)$", message = "field只能为phone或email")
    @Schema(description = "解绑字段：phone 或 email")
    private String field;

    @NotBlank(message = "当前密码不能为空")
    @Schema(description = "当前账户密码（明文）")
    private String password;

}
