package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 个人资料更新DTO
 *
 * @author sz
 * @since 2026-04-30
 */
@Data
@Schema(description = "个人资料更新DTO")
public class UserProfileUpdateDTO {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "性别 0-保密 1-男 2-女")
    private Integer sex;

    @Schema(description = "生日 YYYY-MM-DD")
    private String birthday;

    @Schema(description = "头像 objectKey")
    private String avatar;

}
