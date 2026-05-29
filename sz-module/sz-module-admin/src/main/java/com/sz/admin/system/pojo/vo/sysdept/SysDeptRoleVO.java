package com.sz.admin.system.pojo.vo.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author sz
 * @since 2025/7/15 16:22
 */
@Data
public class SysDeptRoleVO {

    @Schema(description = "选中id数组")
    private List<Long> selectIds;

    @Schema(description = "角色信息数组")
    private List<SysDeptRoleVO.RoleInfoVO> roleInfoVOS;

    @Data
    public static class RoleInfoVO {

        @Schema(description = "角色id")
        private Long id;

        @Schema(description = "角色名称")
        private String roleName;
    }
}
