package com.sz.admin.system.pojo.dto.sysuser;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SysUserQueryDTO
 *
 * @author sz
 * @since 2023/8/23
 */
@Data
public class SysUserListDTO extends PageQuery {

    @Schema(description = "账户")
    private String username;

    @Schema(description = "姓名")
    private String nickname;

    @Schema(description = "部门列表。0查询全部，-1 查询未分配部门的用户")
    private Long deptId;

    @Schema(description = "是否只查询当前层级")
    private Boolean isThisDeep;

    @Schema(description = "电话")
    private String phone;

}
