package com.sz.admin.system.pojo.vo.sysmenu;

import com.sz.core.common.service.Treeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * SysMenuVO
 *
 * @author sz
 * @since 2023/8/21
 */
@Data
public class MenuTreeVO implements Treeable<MenuTreeVO> {

    @Schema(description = "id")
    private Object id;

    @Schema(description = "pid")
    private Object pid;

    @Schema(description = "层级")
    private Long deep;

    @Schema(description = "排序")
    private Long sort;

    @Schema(description = "title")
    private String title;

    @Schema(description = "菜单类型")
    private String menuTypeCd;

    @Schema(description = "按钮权限")
    private String permissions;

    @Schema(description = "子级")
    private List<MenuTreeVO> children;
}
