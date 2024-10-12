package com.sz.admin.system.pojo.vo.sysdept;

import com.sz.core.common.service.Treeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * SysDept查询返回
 * </p>
 *
 * @author sz
 * @since 2024-03-20
 */
@Data
@Schema(description = "SysDept返回vo")
public class SysDeptVO implements Treeable<SysDeptVO> {

    {
        leaders = new ArrayList<>();
        deptIds = new ArrayList<>();
        userIds = new ArrayList<>();
    }

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "父级id")
    private Long pid;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "层级")
    private Long deep;

    @Schema(description = "排序")
    private Long sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否锁定")
    private String isLock;

    @Schema(description = "子级")
    private List<SysDeptVO> children;

    @Schema(description = "负责人")
    private List<Long> leaders;

    @Schema(description = "负责人信息")
    private String leaderInfo;

    @Schema(description = "数据权限")
    private String dataScopeCd;

    @Schema(description = "关联data_scope id")
    private Long scopeId;

    @Schema(description = "数据权限-自定义: 部门项1")
    private List<Long> deptIds;

    @Schema(description = "数据权限-自定义: 用户项")
    private List<Long> userIds;

}