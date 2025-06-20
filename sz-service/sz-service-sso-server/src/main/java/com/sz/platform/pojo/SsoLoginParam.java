package com.sz.platform.pojo;

import com.sz.platform.enums.LoginTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SsoLoginParam - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/6/19
 */
@Data
@Schema(description = "SSO登录参数")
public class SsoLoginParam {

    @Schema(description = "登录类型; USERNAME, PHONE, EMAIL", requiredMode = Schema.RequiredMode.REQUIRED)
    private LoginTypeEnum loginType;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "验证码")
    private String code;

}
