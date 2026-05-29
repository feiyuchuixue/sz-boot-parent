package com.sz.admin.system.pojo.vo.sysuser;

import com.sz.resource.annotation.OssUrlFill;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.sz.platform.constant.AdminSceneCodeConstant.ADMIN_USER_LOGO_SCENE_CODE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户基本资料")
public class UserProfileVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "性别: 0-未知, 1-男, 2-女")
    private Integer sex;

    @Schema(description = "生日: YYYY-MM-DD")
    private String birthday;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @OssUrlFill(sceneCode = ADMIN_USER_LOGO_SCENE_CODE)
    @Schema(description = "头像URL")
    private String avatar;

}
