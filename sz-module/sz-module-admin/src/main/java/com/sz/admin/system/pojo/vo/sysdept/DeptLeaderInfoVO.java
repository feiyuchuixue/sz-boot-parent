package com.sz.admin.system.pojo.vo.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DeptLeaderInfoVO
 *
 * @author sz
 * @version 1.0
 */
@Data
public class DeptLeaderInfoVO {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "负责人ID")
    private Long leaderId;

}
