package com.sz.admin.system.pojo.dto.sysmenu;

import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author sz
 * @date 2023/8/28 15:49
 */
@Data
@Schema(description = "用户角色变更")
public class SysUserRoleDTO {

    @Schema(description = "角色id数组")
    private List<Long> roleIds;

    @NotZero(message = "用户id不能为空")
    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

}
