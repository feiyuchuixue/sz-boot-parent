package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统菜单表
 * </p>
 *
 * @author sz
 * @since 2023-08-21
 */
@Data
@Table("sys_menu")
@Schema(description = "系统菜单表")
public class SysMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "菜单表id")
    private String id;

    @Schema(description = "父级id")
    private String pid;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "name")
    private String name;

    @Schema(description = "title")
    private String title;

    @Schema(description = "icon图标")
    private String icon;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "redirect地址")
    private String redirect;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "层级")
    private Integer deep;

    @Schema(description = "菜单类型 （字典表menu_type）")
    private String menuTypeCd;

    @Schema(description = "按钮权限")
    private String permissions;

    @Schema(description = "是否隐藏")
    private String isHidden;

    @Schema(description = "是否有子级")
    private String hasChildren;

    @Schema(description = "路由外链时填写的访问地址")
    private String isLink;

    @Schema(description = "菜单是否全屏")
    private String isFull;

    @Schema(description = "菜单是否固定在标签页")
    private String isAffix;

    @Schema(description = "当前路由是否缓存")
    private String isKeepAlive;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "更新人")
    private Long updateId;

    @Schema(description = "删除与否")
    @Column(isLogicDelete = true)
    private String delFlag;

    @Schema(description = "菜单是否开启数据权限")
    private String useDataScope;

    @Schema(description = "删除人ID")
    private Long deleteId;

    private LocalDateTime deleteTime;
}
