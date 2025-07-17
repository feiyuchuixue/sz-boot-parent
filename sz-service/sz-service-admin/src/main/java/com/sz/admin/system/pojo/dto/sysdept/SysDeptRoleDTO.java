package com.sz.admin.system.pojo.dto.sysdept;

import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author sz
 * @since 2025/7/15 16:49
 */
@Data
@Schema(description = "部门角色变更")
public class SysDeptRoleDTO {

    @Schema(description = "角色id数组")
    private List<Long> roleIds;

    @NotZero(message = "部门id不能为空")
    @Schema(description = "部门id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long deptId;

}
