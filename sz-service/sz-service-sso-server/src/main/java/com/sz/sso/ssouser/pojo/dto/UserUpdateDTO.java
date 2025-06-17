package com.sz.sso.ssouser.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * User修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-16
 */
@Data
@Schema(description = "User修改DTO")
public class UserUpdateDTO {

    @Schema(description =  "平台用户id，sso client 的 center_id映射")
    private Long id;

    @Schema(description =  "用户名，唯一")
    private String username;

    @Schema(description =  "昵称")
    private String nickname;

    @Schema(description =  "手机号")
    private String phone;

    @Schema(description =  "邮箱")
    private String email;

    @Schema(description =  "密码")
    private String password;

    @Schema(description =  "状态（0：正常，1：禁用，2：待验证）")
    private String status;

    @Schema(description =  "头像地址")
    private String logo;

}