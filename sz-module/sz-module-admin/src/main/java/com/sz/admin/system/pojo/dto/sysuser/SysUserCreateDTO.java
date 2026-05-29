package com.sz.admin.system.pojo.dto.sysuser;

import com.sz.core.common.valid.annotation.IdCard;
import com.sz.core.common.valid.annotation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * SysUserCreateDTO
 *
 * @author sz
 * @since 2023/8/23
 */
@Data
@Schema(description = "SysUser添加DTO")
public class SysUserCreateDTO {

    @NotBlank(message = "账户不能为空")
    @Size(min = 3, max = 32, message = "账户长度须在3到32个字符之间")
    @Pattern(regexp = "^\\w+$", message = "账户只能包含字母、数字和下划线")
    @Schema(description = "账户", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

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

    @Schema(description = "部门ID")
    private Long deptId = -1L;
}
