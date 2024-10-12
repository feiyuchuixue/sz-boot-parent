package com.sz.admin.system.pojo.dto.sysdatarole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * SysDataRole添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-09
 */
@Data
@Schema(description = "SysDataRole添加DTO")
public class SysDataRoleCreateDTO {

    {
        selectMenuIds = new ArrayList<>();
        selectDeptIds = new ArrayList<>();
    }

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "数据范围")
    private String dataScopeCd;

    @Schema(description = "选中的菜单id数组")
    private List<String> selectMenuIds;

    @Schema(description = "选中的部门id数组")
    private List<Long> selectDeptIds;

    @Schema(description = "（自定义）选中的用户id数组")
    private List<Long> userOptions;

}