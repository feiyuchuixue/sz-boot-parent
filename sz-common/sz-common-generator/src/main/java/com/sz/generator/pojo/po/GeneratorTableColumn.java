package com.sz.generator.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.mysql.EntityChangeListener;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 代码生成业务表字段
 * </p>
 *
 * @author sz
 * @since 2023-11-27
 */
@Data
@Table(value = "generator_table_column", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
public class GeneratorTableColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @Id(keyType = KeyType.Auto)
    private Long columnId;

    /**
     * 归属表编号
     */
    private Long tableId;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列描述
     */
    private String columnComment;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * JAVA类型 （String、Integer、Long、Double、BigDecimal、）
     */
    private String javaType;

    /**
     * 搜索类型(input、input-number、select、select-v2、tree-select、cascader、date-picker、time-picker、time-select、switch、slider)
     */
    private String searchType;

    /**
     * ts类型 (string、number、string[])
     */
    private String tsType;

    /**
     * java类型包名(java)
     */
    private String javaTypePackage;

    /**
     * JAVA字段名
     */
    private String javaField;

    /**
     * get开头的驼峰字段名
     */
    private String upCamelField;

    /**
     * 是否主键（1是）
     */
    private String isPk;

    /**
     * 是否自增（1是）
     */
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    private String isQuery;

    /**
     * 是否自动填充(1 是)，通用属性的自动填充，需代码配合（create_id、create_time、update_id、update_time）
     */
    private String isAutofill;

    /**
     * 是否进行唯一校验(1 是)，字段唯一性校验
     */
    private String isUniqueValid;

    /**
     * 自动填充类型(INSERT/UPDATE/INSERT_UPDATE)
     */
    private String autofillType;

    /**
     * 查询方式（等于、不等于、大于、小于、范围）
     */
    private String queryType;

    /**
     * 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）
     */
    private String htmlType;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 是否逻辑删除(1 是)
     */
    private String isLogicDel;

    /**
     * 其他设置
     */
    private String options;

    /**
     * 排序
     */
    private Integer sort;

    private Long createId;

    private Long updateId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 是否导入字段(1 是)
     */
    private String isImport;

    /**
     * 是否导出字段(1 是)
     */
    private String isExport;

    /**
     * 字典展示方式（0 唯一标识；1 别名）
     */
    private String dictShowWay;
}
