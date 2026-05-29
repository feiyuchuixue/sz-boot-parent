package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * UserDeptDTO
 * 
 * @author sz
 * @since 2024/4/2 9:44
 * @version 1.0
 */
@Data
public class UserDeptDTO {

    @Schema(description = "用户id数组")
    private List<Long> userIds = new ArrayList<>();

    @Schema(description = "部门id数组")
    private List<Long> deptIds = new ArrayList<>();
}
