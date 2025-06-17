package com.sz.ssoadmin.system.pojo.vo.sysdept;

import com.sz.core.common.service.Treeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DeptTreeVO
 * 
 * @author sz
 * @since 2024/3/22 9:59
 * @version 1.0
 */

@Data
public class DeptTreeVO implements Treeable<DeptTreeVO> {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "pid")
    private Long pid;

    @Schema(description = "层级")
    private Long deep;

    @Schema(description = "排序")
    private Long sort;

    @Schema(description = "子级")
    private List<DeptTreeVO> children;

    @Schema(description = "name")
    private String name;

    @Schema(description = "用户数量")
    private Long userTotal = 0L;

}
