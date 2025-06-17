package com.sz.ssoadmin.system.pojo.vo.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * TotalDeptVO
 * 
 * @author sz
 * @since 2024/4/8 14:21
 * @version 1.0
 */
@Data
public class TotalDeptVO {

    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "部门人员数统计")
    private Long total;
}
