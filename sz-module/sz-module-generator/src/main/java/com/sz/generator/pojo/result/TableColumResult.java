package com.sz.generator.pojo.result;

import lombok.Data;

/**
 * @author sz
 * @since 2023/11/27 15:30
 */
@Data
public class TableColumResult {

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 是否必填
     */
    private String isRequired;

    /**
     * 是否主键
     */
    private String isPk;

    /**
     * 排序
     */
    private int sort;

    /**
     * comment描述
     */
    private String columnComment;

    /**
     * 是否自增
     */
    private String isIncrement;

    /**
     * 字段类型
     */
    private String columnType;

}
