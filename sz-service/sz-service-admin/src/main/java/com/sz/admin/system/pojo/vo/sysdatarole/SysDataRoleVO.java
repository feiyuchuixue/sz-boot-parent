package com.sz.admin.system.pojo.vo.sysdatarole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * SysDataRole返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-09
 */
@Data
@Schema(description = "SysDataRole返回vo")
public class SysDataRoleVO {

    @Schema(description = "角色id")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "数据范围")
    private String dataScopeCd;

    @Schema(description = "简介")
    private String remark;

    @Schema(description = "是否锁定")
    private String isLock;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "选中的菜单id数组")
    private List<String> selectMenuIds;

    @Schema(description = "选中的部门id数组")
    private List<Long> selectDeptIds;

    @Schema(description = "（自定义）选中的用户id数组")
    private List<Long> userOptions;

}