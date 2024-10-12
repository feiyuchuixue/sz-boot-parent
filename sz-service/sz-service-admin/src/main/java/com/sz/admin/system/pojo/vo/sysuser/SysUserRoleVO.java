package com.sz.admin.system.pojo.vo.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author sz
 * @date 2023/8/29 10:47
 */
@Data
public class SysUserRoleVO {

    @Schema(description = "选中id数组")
    private List<Long> selectIds;

    @Schema(description = "角色信息数组")
    private List<RoleInfoVO> roleInfoVOS;

    @Data
    public static class RoleInfoVO {

        @Schema(description = "角色id")
        private Long id;

        @Schema(description = "角色名称")
        private String roleName;
    }

}
