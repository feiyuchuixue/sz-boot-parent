package com.sz.generator.pojo.dto;

import lombok.Data;

/**
 * @ClassName MenuCreateDTO
 * @Author sz
 * @Date 2023/12/21 15:31
 * @Version 1.0
 */
@Data
public class MenuCreateDTO {

    {
        pid = "0";
    }

    private String id;

    private String pid;

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
