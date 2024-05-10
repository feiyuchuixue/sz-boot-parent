package com.sz.applet.miniuser.pojo.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName MiniLoginUser
 * @Author sz
 * @Date 2024/4/28 14:15
 * @Version 1.0
 */
@Data
public class MiniLoginUser {

    @Schema(description = "用户ID(MiniUser表主键)")
    private Long userId;

    @Schema(description = "小程序openid")
    private String openid;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号,手机号为空则未绑定")
    private String phone;

    @Schema(description = "LOGO")
    private String logo;

    @Schema(description = "邮箱，邮箱为空则未绑定")
    private String email;

}
