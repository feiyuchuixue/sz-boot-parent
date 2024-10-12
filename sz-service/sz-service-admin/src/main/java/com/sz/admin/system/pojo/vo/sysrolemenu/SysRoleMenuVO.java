package com.sz.admin.system.pojo.vo.sysrolemenu;

import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sz
 * @date 2023/8/29 9:22
 */
@Data
@Schema(description = "角色菜单详情")
public class SysRoleMenuVO {

    @Schema(description = "菜单列表")
    private List<MenuTreeVO> menuLists;

    @Schema(description = "选中的菜单id")
    private List<String> selectIds = new ArrayList<>();

}
