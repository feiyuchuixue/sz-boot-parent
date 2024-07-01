package com.sz.admin.system.pojo.dto.sysdept;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SysDept添加DTO
 * </p>
 *
 * @author sz
 * @since 2024-03-20
 */
@Data
@Schema(description = "SysDept修改DTO")
public class SysDeptUpdateDTO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父级id")
    private Long pid;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "负责人")
    private List<Long> leaders;

    @Schema(description = "数据权限类型")
    private String dataScopeCd;

    @Schema(description = "数据权限-自定义部门")
    private List<Long> deptIds;

    @Schema(description = "数据权限-自定义用户")
    private List<Long> userIds;


}