package com.sz.admin.system.pojo.dto.sysuser;

import com.sz.core.common.valid.annotation.IdCard;
import com.sz.core.common.valid.annotation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Phone
    @Schema(description = "手机号")
    private String phone;

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String logo;

    @Schema(description = "性别")
    private Integer sex;

    @IdCard
    @Schema(description = "身份证")
    private String idCard;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "状态")
    private String accountStatusCd;

    @Schema(description = "标签")
    private String userTagCd;

    @Schema(description = "生日")
    private String birthday;
}
