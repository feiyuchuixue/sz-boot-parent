package com.sz.admin.system.pojo.dto.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "id不能为空")
    @Schema(description = "id")
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    @Schema(description = "部门名称")
    private String name;

    @NotNull(message = "父级id不能为空")
    @Schema(description = "父级id")
    private Long pid;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "负责人")
    private List<Long> leaders;

}