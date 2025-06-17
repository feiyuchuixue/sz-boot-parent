package com.sz.ssoadmin.system.pojo.vo.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserDeptVO
 * 
 * @author sz
 * @since 2024/4/8 9:04
 * @version 1.0
 */
@Data
public class UserDeptInfoVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "部门信息,多个逗号分隔")
    private String deptInfos;

    @Schema(description = "部门ID,多个逗号分隔")
    private String deptIds;

}
