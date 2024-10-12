package com.sz.admin.system.pojo.dto.sysmenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @date 2023/9/1 14:07
 */
@Data
@Schema(description = "菜单权限查询")
public class MenuPermissionDTO {

    @Schema(description = "菜单id")
    private String id;

    @Schema(description = "权限标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String permissions;

}
