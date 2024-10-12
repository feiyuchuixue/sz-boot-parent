package com.sz.admin.system.pojo.vo.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName UserRoleInfoVO
 * @Author sz
 * @Date 2024/4/9 11:00
 * @Version 1.0
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
