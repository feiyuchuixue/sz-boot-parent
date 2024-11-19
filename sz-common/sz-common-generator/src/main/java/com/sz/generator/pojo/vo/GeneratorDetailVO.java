package com.sz.generator.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName GeneratorDetailVO
 * @Author sz
 * @Date 2023/11/29 15:25
 * @Version 1.0
 */
@Schema(description = "代码生成配置详情")
@Data
public class GeneratorDetailVO {

    @Schema(description = "基本信息")
    private BaseInfo baseInfo;

    @Schema(description = "生成信息")
    private GeneratorInfo generatorInfo;

    @Schema(description = "column字段信息")
    private List<Column> columns;

    @Data
    public static class BaseInfo {

        @Schema(description = "table_id")
        private Long tableId;

        @Schema(description = "表名称")
        private String tableName;

        @Schema(description = "表描述")
        private String tableComment;

        @Schema(description = "实体类名称")
        private String ClassName;

        @Schema(description = "camel实体类名称")
        private String camelClassName;

        @Schema(description = "作者")
        private String functionAuthor;

        @Schema(description = "备注")
        private String remark;
    }

    @Data
    public static class GeneratorInfo {

        @Schema(description = "模板")
        private String tplCategory;

        @Schema(description = "包路径")
        private String packageName;

        @Schema(description = "模块名")
        private String moduleName;

        @Schema(description = "业务名")
        private String businessName;

        @Schema(description = "功能名")
        private String functionName;

        @Schema(description = "其他参数。如上级菜单")
        private String options;

        @Schema(description = "代码生成方式")
        private String type;

        @Schema(description = "上级菜单id")
        private String parentMenuId;

        @Schema(description = "api生成路径")
        private String pathApi;

        @Schema(description = "web生成路径")
        private String pathWeb;

        @Schema(description = "是否自动创建菜单路由（1 是）")
        private String menuInitType;

        @Schema(description = "是否自动创建按钮权限 (1 是)")
        private String btnPermissionType;

        @Schema(description = "是否支持导入(1 是)")
        private String hasImport;

        @Schema(description = "是否支持导出(1 是)")
        private String hasExport;

        @Schema(description = "生成类型")
        private String generateType;

        @Schema(description = "是否自动填充(1 是)")
        private String isAutofill;
    }

    @Data
    public static class Column {

        @Schema(description = "column_id")
        private Long columnId;

        @Schema(description = "table_id")
        private Long tableId;

        @Schema(description = "列名称")
        private String columnName;

        @Schema(description = "列描述")
        private String columnComment;

        @Schema(description = "列类型")
        private String columnType;

        @Schema(description = "JAVA类型 （String、Integer、Long、Double、BigDecimal、Date、LocalDateTime、LocalDate、LocalTime）")
        private String javaType;

        @Schema(description = "ts类型")
        private String tsType;

        @Schema(description = "搜索类型(input、input-number、select、select-v2、tree-select、cascader、date-picker、time-picker、time-select、switch、slider)")
        private String searchType;

        @Schema(description = "JAVA类型 （引包）")
        private String javaTypePackage;

        @Schema(description = "JAVA特殊包类型 （引包）")
        private String specialPackages;

        @Schema(description = "JAVA字段名")
        private String javaField;

        @Schema(description = "get开头的驼峰字段名")
        private String upCamelField;

        @Schema(description = "是否主键（1是）")
        private String isPk;

        @Schema(description = "是否自增（1是）")
        private String isIncrement;

        @Schema(description = "是否必填（1是）")
        private String isRequired;

        @Schema(description = "是否为插入字段（1是）")
        private String isInsert;

        @Schema(description = "是否编辑字段（1是）")
        private String isEdit;

        @Schema(description = "是否列表字段（1是）")
        private String isList;

        @Schema(description = "是否查询字段（1是）")
        private String isQuery;

        @Schema(description = "是否自动填充(1 是)，通用属性的自动填充，需代码配合（create_id、create_time、update_id、update_time）")
        private String isAutofill;

        @Schema(description = "是否进行唯一校验(1 是)，字段唯一性校验")
        private String isUniqueValid;

        @Schema(description = "查询方式（等于EQ、不等于NEQ、大于GT、小于LT、范围BETWEEN、大于等于GTE、小于等于LTE）")
        private String queryType;

        @Schema(description = "显示类型（input、textarea、select、radio、checkbox、datetime、date、time、imageUpload、fileUpload、editor）")
        private String htmlType;

        @Schema(description = "字典类型")
        private String dictType;

        @Schema(description = "其他设置")
        private String options;

        @Schema(description = "是否逻辑删除(1 是)")
        private String isLogicDel;

        @Schema(description = "自动填充类型(FieldFill.INSERT/FieldFill.UPDATE/FieldFill.INSERT_UPDATE)")
        private String autofillType;

        @Schema(description = "是否导入字段(1 是)")
        private String isImport;

        @Schema(description = "是否导出字段(1 是)")
        private String isExport;

        @Schema(description = "字典展示方式（0 唯一标识；1 别名）")
        private String dictShowWay;
    }

}
