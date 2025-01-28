package com.sz.generator.pojo.dto;

import lombok.Data;

/**
 * @author sz
 * @since 2023/12/21 15:31
 */
@Data
public class MenuCreateDTO {

    private String id;

    private String pid = "0";

    private String path;

    private String name;

    private String title;

    private String icon;

    private String component;

    private Integer sort;

    private Integer deep;

    private String menuTypeCd;

    private String permissions;

    private String hasChildren;

}
