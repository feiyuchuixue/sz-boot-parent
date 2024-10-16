package com.sz.admin.system.pojo.vo.sysmenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @date 2023/9/1 15:32
 */
@Data
public class MenuPermissionVO {

    {
        permissionCount = 0;
    }

    @Schema(description = "权限标识数量")
    private int permissionCount;

}
