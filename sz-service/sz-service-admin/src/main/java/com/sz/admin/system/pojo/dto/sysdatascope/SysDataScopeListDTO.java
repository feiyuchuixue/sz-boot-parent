package com.sz.admin.system.pojo.dto.sysdatascope;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * SysDataScope查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-01
 */
@Data
@Schema(description = "SysDataScope查询DTO")
@RequiredArgsConstructor
@AllArgsConstructor
public class SysDataScopeListDTO {

    @Schema(description = "关联类型，data_scope_relation_type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1007001")
    private String relationTypeCd;

    @Schema(description = "关联表id，与关联类型联动。例relationTypeCd=1007001代表部门权限，relationId代表deptId；relationTypeCd=1007002代表个人权限，relationId代表userId", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long relationId;

}