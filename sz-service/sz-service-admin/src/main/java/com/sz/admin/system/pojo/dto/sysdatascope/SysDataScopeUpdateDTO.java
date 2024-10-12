package com.sz.admin.system.pojo.dto.sysdatascope;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * <p>
 * SysDataScope修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-01
 */
@Data
@Schema(description = "SysDataScope修改DTO")
@RequiredArgsConstructor
@AllArgsConstructor
public class SysDataScopeUpdateDTO {

    @Schema(description = "关联类型，data_scope_relation_type")
    private String relationTypeCd;

    @Schema(description = "关联表id")
    private Long relationId;

    @Schema(description = "关联部门")
    private List<Long> deptOptions;

    @Schema(description = "关联用户")
    private List<Long> userOptions;

    @Schema(description = "数据权限")
    private String dataScope;

}