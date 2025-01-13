package com.sz.generator.pojo.result;

import lombok.Data;

/**
 * @author sz
 * @since 2023/11/27 13:55
 */
@Data
public class TableResult {

    private String tableName;

    private String tableComment;

    private String createTime;

    private String updateTime;

}
