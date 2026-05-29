package com.sz.admin.system.pojo.vo.sysuser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sz.resource.annotation.OssUrlFill;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.sz.platform.constant.AdminSceneCodeConstant.ADMIN_USER_LOGO_SCENE_CODE;

/**
 * @author sz
 * @since 2023/8/24 10:29
 */
@Data
public class SysUserVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "username")
    private String username;

    @JsonIgnore // 忽略该字段的序列化和反序列化
    @Schema(description = "密码")
    private String pwd;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "昵称")
    private String nickname;

    @OssUrlFill(sceneCode = ADMIN_USER_LOGO_SCENE_CODE)
    @Schema(description = "LOGO")
    private String logo;

    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "身份证")
    private String idCard;

    @Schema(description = "email")
    private String email;

    @Schema(description = "账号状态码")
    private String accountStatusCd;

    @Schema(description = "userTagCd")
    private String userTagCd;

    @Schema(description = "最近一次登录时间")
    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String delFlag;

    @Schema(description = "部门ID，多个逗号分隔")
    private String deptIds;

    @Schema(description = "角色ID,多个逗号分隔")
    private String roleIds;

}
