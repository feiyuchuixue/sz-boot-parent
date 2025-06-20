package com.sz.ssoserver.ssouser.pojo.dto;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SsoUser查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
@Data
@Schema(description = "SsoUser查询DTO")
public class SsoUserListDTO extends PageQuery {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户状态")
    private String platformUserStatusCd;

}