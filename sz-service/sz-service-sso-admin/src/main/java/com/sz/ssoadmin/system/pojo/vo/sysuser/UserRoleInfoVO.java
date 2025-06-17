package com.sz.ssoadmin.system.pojo.vo.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserRoleInfoVO
 * 
 * @author sz
 * @since 2024/4/9 11:00
 * @version 1.0
 */
@Data
public class UserRoleInfoVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色信息,多个逗号分隔")
    private String roleInfos;

    @Schema(description = "角色ID")
    private String roleIds;

}
