package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SysUserUpdateDTO
 *
 * @author sz
 * @since 2023/8/23
 */
@Data
@Schema(description = "SysUser修改DTO")
public class SysUserUpdateDTO {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String logo;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "身份证")
    private String idCard;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "状态")
    private String accountStatusCd;

    @Schema(description = "标签")
    private String userTagCd;

    @Schema(description = "生日")
    private String birthday;
}
