package com.sz.admin.system.pojo.vo.sysmenu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * SysMenuVO
 *
 * @author sz
 * @since 2023/8/21
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SysMenuVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "pid")
    private String pid;

    @Schema(description = "路径")
    private String path;

    private String name;

    private Integer sort;

    @JsonIgnore
    private String redirect;

    @Schema(description = "组件路径")
    private String component;

    @JsonIgnore
    private String icon;

    @JsonIgnore
    private String title;

    @JsonIgnore
    private String isLink;

    @JsonIgnore
    private String isHidden;

    @JsonIgnore
    private String isFull;

    @JsonIgnore
    private String isAffix;

    @JsonIgnore
    private String useDataScope;

    @JsonIgnore
    private String isKeepAlive;

    @Schema(description = "元数据")
    private Meta meta;

    private List<SysMenuVO> children;

    @Schema(description = "权限标识")
    private String permissions;

    @Schema(description = "菜单类型")
    private String menuTypeCd;

    @Data
    public static class Meta {

        @Schema(description = "菜单和面包屑对应的图标")
        private String icon;

        @Schema(description = "路由标题 (用作 document.title || 菜单的名称)")
        private String title;

        @Schema(description = "路由外链时填写的访问地址")
        private String isLink;

        @Schema(description = "是否在菜单中隐藏 (通常列表详情页需要隐藏)")
        private String isHidden;

        @Schema(description = "菜单是否全屏 (示例：数据大屏页面)")
        private String isFull;

        @Schema(description = "菜单是否固定在标签页中")
        private String isAffix;

        @Schema(description = "当前路由是否缓存")
        private String isKeepAlive;

        @Schema(description = "菜单是否开启数据权限")
        private String useDataScope;
    }
}
