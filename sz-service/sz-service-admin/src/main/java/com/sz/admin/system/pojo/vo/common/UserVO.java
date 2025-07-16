package com.sz.admin.system.pojo.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserVO -用户信息返回VO
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/6
 */
@Schema(description = "用户信息返回VO")
@Data
public class UserVO {

    @Schema(description = "用户ID")
    private Object id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String name;

    @Schema(description = "用户手机号")
    private String phone;

}
