package com.sz.generator.pojo.result;

import lombok.Data;

/**
 * @ClassName SysMenuResult
 * @Author sz
 * @Date 2023/12/21 14:40
 * @Version 1.0
 */
@Data
public class SysMenuResult {

    private String id;

    private String pid;

    private String path;

    private String name;

    private String title;

    private String icon;

    private String component;

    private String redirect;

    private Integer sort;

    private Integer deep;

    private String menuTypeCd;

    private String permissions;

    private String isHidden;

    private String hasChildren;

    private String isLink;

    private String isFull;

    private String isAffix;

    private String isKeepAlive;

    private String delFlag;

}
