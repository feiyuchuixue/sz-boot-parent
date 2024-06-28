package com.sz.core.datascope;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 定义了数据权限的范围，用于控制不同用户对数据的访问权限。
 * 这个类是数据权限管理的核心，通过定义不同的数据权限类型，
 * 可以灵活地控制用户对数据的访问。
 */
@Data
@Schema(description = "数据权限范围")
public class DataScope {

    {
        scope = "1006001"; // 默认查询全部数据，不限制
        columnName = " "; // 默认字段名
    }

    public DataScope() {
    }

    public DataScope(String scope, Class<?> tableClass, String columnName) {
        this.scope = scope;
        this.columnName = columnName;
        this.tableClass = tableClass;
    }

    public DataScope(String scope, Class<?> tableClass) {
        this.scope = scope;
        this.tableClass = tableClass;
    }


    /**
     * 数据权限范围类型，定义了不同的数据访问级别。
     */
    @Schema(description = "数据权限范围类型")
    private String scope;

    /**
     * 指定权限控制的字段名，用于确定数据权限的具体作用对象。 蛇形，例如： create_id
     */
    @Schema(description = "字段名")
    private String columnName;

    /**
     * 对应的数据表的实体类，用于确定数据权限作用的数据表。这个类应该是一个持久化对象类，代表了数据库中的一张表。
     */
    @Schema(description = "实体PO类")
    private Class<?> tableClass;

}
