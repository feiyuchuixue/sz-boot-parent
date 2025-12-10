package com.sz.generator.core.util;

import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.Utils;
import com.sz.generator.core.GeneratorConstants;
import com.sz.generator.pojo.po.GeneratorTable;
import com.sz.generator.pojo.po.GeneratorTableColumn;
import com.sz.generator.pojo.property.GeneratorProperties;
import com.sz.generator.pojo.result.TableColumResult;
import com.sz.generator.pojo.result.TableResult;
import com.sz.core.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * @author sz
 * @since 2023/11/28 16:06
 */
public class GeneratorUtils {

    private GeneratorUtils() {
        throw new IllegalStateException("GeneratorUtils class Illegal");
    }

    public static GeneratorTable initGeneratorTable(TableResult table) {
        return initGeneratorTable(table, false, null);
    }

    public static GeneratorTable initGeneratorTable(TableResult table, boolean ignoreTablePrefix, String[] prefixes) {

        GeneratorProperties prop = SpringApplicationContextUtils.getInstance().getBean(GeneratorProperties.class);
        String author = prop.getGlobal().getAuthor();
        String packages = prop.getGlobal().getPackages();

        String tableName = table.getTableName();
        // 如果需要将表前缀去掉
        if (ignoreTablePrefix && prefixes != null) {
            for (String prefix : prefixes) {
                if (tableName.startsWith(prefix)) {
                    tableName = tableName.replaceFirst(prefix, "");
                    break;
                }
            }
        }

        GeneratorTable generatorTable = new GeneratorTable();
        generatorTable.setTableName(table.getTableName());
        generatorTable.setTableComment(table.getTableComment());
        generatorTable.setPackageName(packages);
        generatorTable.setModuleName(tableName.replace("_", ""));
        generatorTable.setClassName(GeneratorUtils.toUpCase(tableName));
        generatorTable.setCamelClassName(StringUtils.toCamelCase(tableName));
        generatorTable.setTplCategory(GeneratorConstants.TPL_CRUD);
        generatorTable.setBusinessName(GeneratorUtils.toCamelCase(tableName));
        generatorTable.setFunctionName(table.getTableComment());
        generatorTable.setFunctionAuthor(author);
        generatorTable.setType("0");
        generatorTable.setPath("/");
        generatorTable.setParentMenuId("0"); // 默认父级菜单id设置为根目录

        return generatorTable;
    }

    public static GeneratorTableColumn initColumnField(TableColumResult column, Long tableId, int i) {
        GeneratorTableColumn tableColumn = new GeneratorTableColumn();
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        tableColumn.setTableId(tableId);
        tableColumn.setColumnName(columnName);
        tableColumn.setColumnComment(column.getColumnComment());
        tableColumn.setColumnType(column.getColumnType());
        tableColumn.setJavaType(GeneratorConstants.TYPE_STRING);
        tableColumn.setTsType(GeneratorConstants.TS_TYPE_STRING);
        tableColumn.setJavaField(StringUtils.toCamelCase(columnName));
        tableColumn.setUpCamelField(GeneratorUtils.toUpCase(columnName));
        tableColumn.setIsPk(column.getIsPk());
        tableColumn.setIsIncrement(column.getIsIncrement());
        tableColumn.setQueryType(GeneratorConstants.QUERY_EQ);
        tableColumn.setSort(i);
        tableColumn.setDictType("");
        setRequiredValue(column, tableColumn);
        setHtmlAndJavaType(dataType, column.getColumnType(), tableColumn);
        setColumnAttributes(columnName, tableColumn);

        // 如果是主键并且是int行，将java类型设置为Long
        if (!isNotPrimaryKey(tableColumn.getIsPk()) && (("int").equals(tableColumn.getColumnType()) || ("bigint").equals(tableColumn.getColumnType()))) {
            tableColumn.setJavaType(GeneratorConstants.TYPE_LONG);
        }
        // 将bigint类型映射成long
        if (("bigint").equals(tableColumn.getColumnType())) {
            tableColumn.setJavaType(GeneratorConstants.TYPE_LONG);
        }
        // 【约定】： 使用create_id, update_id, delete_id change更新时，强制类型Long
        if (("create_id").equals(columnName) || ("update_id").equals(columnName) || ("delete_id").equals(columnName)) {
            tableColumn.setJavaType(GeneratorConstants.TYPE_LONG);
        }
        // 【约定】： 使用del_flag 作为逻辑删除标识字段
        if ("del_flag".equals(columnName)) {
            tableColumn.setIsLogicDel(GeneratorConstants.REQUIRE);
        }

        // 【约定】： 使用attachments 或 url 作为 附件字段，数据库类型json， java 类型使用List<UploadResult>
        if ("attachments".equals(columnName) || "url".equals(columnName)) {
            tableColumn.setJavaType(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
        }

        if (tableColumn.getHtmlType() == null) {
            tableColumn.setHtmlType("");
        }
        return tableColumn;
    }

    /**
     * 校验数组是否包含指定值
     *
     * @param arr
     *            数组
     * @param targetValue
     *            值
     * @return 是否包含
     */
    public static boolean arraysContains(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType
     *            列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType) {
        if (indexOf(columnType, "(") > 0) {
            return substringBefore(columnType, "(");
        } else {
            return columnType;
        }
    }

    /**
     * 获取字段长度
     *
     * @param columnType
     *            列类型
     * @return 截取后的列类型
     */
    public static Integer getColumnLength(String columnType) {
        if (indexOf(columnType, "(") > 0) {
            String length = substringBetween(columnType, "(", ")");
            return Integer.valueOf(length);
        } else {
            return 0;
        }
    }

    public static String toUpCase(String input) {
        StringBuilder result = new StringBuilder();
        // 标记是否要将下一个字符转为大写
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            } else {
                // 非字母数字字符后面的字母需要大写
                capitalizeNext = true;
            }
        }
        return result.toString();
    }

    public static String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        // 标记是否要将下一个字符转为大写
        boolean capitalizeNext = false;
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            } else {
                // 非字母数字字符后面的字母需要大写
                capitalizeNext = true;
            }
        }
        return result.toString();
    }

    private static void setRequiredValue(TableColumResult column, GeneratorTableColumn tableColumn) {
        if (Utils.isNotNull(column.getIsRequired())) {
            tableColumn.setIsRequired(column.getIsRequired());
        } else {
            tableColumn.setIsRequired("0");
        }
    }

    private static void setHtmlAndJavaType(String dataType, String columnType, GeneratorTableColumn tableColumn) {
        switch (dataType.toUpperCase()) {
            case "VARCHAR" :
            case "CHAR" :
            case "NVARCHAR" :
            case "VARCHAR2" :
            case "TEXT" :
            case "TINYTEXT" :
            case "MEDIUMTEXT" :
            case "LONGTEXT" :
                setStringTypeAttributes(columnType, tableColumn);
                tableColumn.setSearchType("input");
                break;
            case "TIME" :
                tableColumn.setJavaType(GeneratorConstants.TYPE_LOCALTIME);
                tableColumn.setHtmlType(GeneratorConstants.HTML_TIME);
                tableColumn.setJavaTypePackage("java.time.LocalTime");
                tableColumn.setSearchType(GeneratorConstants.HTML_TIME_PICKER);
                tableColumn.setQueryType(GeneratorConstants.QUERY_BETWEEN);
                break;
            case "DATE" :
                tableColumn.setJavaType(GeneratorConstants.TYPE_LOCAL_DATE);
                tableColumn.setHtmlType(GeneratorConstants.HTML_DATE);
                tableColumn.setJavaTypePackage("java.time.LocalDate");
                tableColumn.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
                tableColumn.setQueryType(GeneratorConstants.QUERY_BETWEEN);
                break;
            case "TIMESTAMP" :
            case "DATETIME" :
                tableColumn.setJavaType(GeneratorConstants.TYPE_LOCAL_DATETIME);
                tableColumn.setHtmlType(GeneratorConstants.HTML_DATETIME);
                tableColumn.setJavaTypePackage("java.time.LocalDateTime");
                tableColumn.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
                tableColumn.setQueryType(GeneratorConstants.QUERY_BETWEEN);
                break;
            case "TINYINT" :
            case "SMALLINT" :
            case "MEDIUMINT" :
            case "INT" :
            case "NUMBER" :
            case "INTEGER" :
            case "BIT" :
            case "BIGINT" :
            case "FLOAT" :
            case "DOUBLE" :
            case "DECIMAL" :
                tableColumn.setTsType(GeneratorConstants.TS_TYPE_NUMBER);
                setNumberTypeAttributes(columnType, tableColumn);
                break;
            default :
                // Handle other data types as needed
        }
    }

    private static void setStringTypeAttributes(String columnType, GeneratorTableColumn tableColumn) {
        Integer columnLength = getColumnLength(columnType);
        String htmlType = columnLength >= 500 ? GeneratorConstants.HTML_TEXTAREA : GeneratorConstants.HTML_INPUT;
        tableColumn.setHtmlType(htmlType);
    }

    private static void setNumberTypeAttributes(String columnType, GeneratorTableColumn tableColumn) {
        String[] str = split(substringBetween(columnType, "(", ")"), ",");
        if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0) {
            tableColumn.setJavaType(GeneratorConstants.TYPE_BIG_DECIMAL);
            tableColumn.setJavaTypePackage("java.math.BigDecimal");
        } else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10) {
            tableColumn.setJavaType(GeneratorConstants.TYPE_INTEGER);
        } else {
            tableColumn.setJavaType(GeneratorConstants.TYPE_INTEGER);
        }
        tableColumn.setHtmlType(GeneratorConstants.HTML_INPUT_NUMBER);
    }

    private static void setColumnAttributes(String columnName, GeneratorTableColumn tableColumn) {
        boolean notPk = isNotPrimaryKey(tableColumn.getIsPk());
        setInsertAttribute(notPk, columnName, tableColumn);
        setEditAttribute(notPk, columnName, tableColumn);
        setListAttribute(notPk, columnName, tableColumn);
        setQueryAttribute(notPk, columnName, tableColumn);
        setQueryType(columnName, tableColumn);
        setAutofillType(columnName, tableColumn);
        setHtmlType(columnName, tableColumn);
        setFillOptions(columnName, tableColumn);
        setFileUploadOptions(columnName, tableColumn);
        setJoditEditorOptions(columnName, tableColumn);
        setImportAttribute(notPk, columnName, tableColumn);
        setExportAttribute(notPk, columnName, tableColumn);
    }

    private static void setQueryType(String columnName, GeneratorTableColumn tableColumn) {
        if (endsWithIgnoreCase(columnName, "name")) {
            tableColumn.setQueryType(GeneratorConstants.QUERY_LIKE);
        }
    }

    private static void setHtmlType(String columnName, GeneratorTableColumn tableColumn) {
        String lowerCaseColumnName = columnName.toLowerCase();
        tableColumn.setSearchType("input");
        if (lowerCaseColumnName.contains("status")) {
            tableColumn.setHtmlType(GeneratorConstants.HTML_RADIO);
            tableColumn.setSearchType("select");
        } else if (lowerCaseColumnName.contains("type") || lowerCaseColumnName.contains("sex") || lowerCaseColumnName.contains("_cd")) {
            tableColumn.setHtmlType(GeneratorConstants.HTML_SELECT);
            tableColumn.setSearchType("select");
        } else if (lowerCaseColumnName.contains("image")) {
            tableColumn.setHtmlType(GeneratorConstants.HTML_IMAGE_UPLOAD);
        } else if (lowerCaseColumnName.contains("file")) {
            tableColumn.setHtmlType(GeneratorConstants.HTML_FILE_UPLOAD);
        } else if (lowerCaseColumnName.contains("content")) {
            tableColumn.setHtmlType(GeneratorConstants.HTML_EDITOR);
        } else if (lowerCaseColumnName.contains("time")) {
            tableColumn.setSearchType("date-picker");
        }
    }

    private static void setListAttribute(boolean notPk, String columnName, GeneratorTableColumn tableColumn) {
        if (!arraysContains(GeneratorConstants.NON_DISPLAYABLE_COLUMNS, columnName) && notPk) {
            tableColumn.setIsList(GeneratorConstants.REQUIRE);
        }
        if (!notPk) {
            tableColumn.setIsList(GeneratorConstants.REQUIRE);
        }
    }

    private static void setImportAttribute(boolean notPk, String columnName, GeneratorTableColumn tableColumn) {
        String htmlType = tableColumn.getHtmlType();
        if (!arraysContains(GeneratorConstants.NON_DISPLAYABLE_IMPORT_COLUMNS, columnName) && notPk && !GeneratorConstants.HTML_EDITOR.equals(htmlType)) {
            tableColumn.setIsImport(GeneratorConstants.REQUIRE);
        }
    }

    private static void setExportAttribute(boolean notPk, String columnName, GeneratorTableColumn tableColumn) {
        String htmlType = tableColumn.getHtmlType();
        if (!arraysContains(GeneratorConstants.NON_EXPORTABLE_COLUMNS, columnName) && notPk && !GeneratorConstants.HTML_EDITOR.equals(htmlType)) {
            tableColumn.setIsExport(GeneratorConstants.REQUIRE);
        }
    }

    private static void setQueryAttribute(boolean notPk, String columnName, GeneratorTableColumn tableColumn) {
        if (!arraysContains(GeneratorConstants.NON_QUERYABLE_COLUMNS, columnName) && notPk) {
            tableColumn.setIsQuery(GeneratorConstants.REQUIRE);
        }
    }

    public static boolean isNotPrimaryKey(String isPk) {
        return !"1".equals(isPk);
    }

    private static void setEditAttribute(boolean notPk, String columnName, GeneratorTableColumn tableColumn) {
        if (!arraysContains(GeneratorConstants.NON_EDITABLE_COLUMNS, columnName) && notPk) {
            tableColumn.setIsEdit(GeneratorConstants.REQUIRE);
        }
        if (!notPk) {
            tableColumn.setIsEdit(GeneratorConstants.REQUIRE);
        }
    }

    private static void setInsertAttribute(boolean notPk, String columnName, GeneratorTableColumn tableColumn) {
        if (!arraysContains(GeneratorConstants.NON_INSERTABLE_COLUMNS, columnName) && notPk) {
            tableColumn.setIsInsert(GeneratorConstants.REQUIRE);
        }
    }

    private static void setAutofillType(String columnName, GeneratorTableColumn tableColumn) {
        if (arraysContains(GeneratorConstants.AUTO_FILL_ON_INSERT_COLUMNS, columnName)) {
            tableColumn.setIsAutofill(GeneratorConstants.REQUIRE);
            tableColumn.setAutofillType("FieldFill.INSERT");
        }
        if (arraysContains(GeneratorConstants.AUTO_FILL_ON_UPDATE_COLUMNS, columnName)) {
            tableColumn.setIsAutofill(GeneratorConstants.REQUIRE);
            tableColumn.setAutofillType("FieldFill.UPDATE");
        }
    }

    private static void setFillOptions(String columnName, GeneratorTableColumn tableColumn) {
        if (arraysContains(GeneratorConstants.AUTO_FILL_OPTIONS_COLUMNS, columnName)) {
            tableColumn.setDictType("dynamic_user_options"); // 设置[动态字典]用户信息
            tableColumn.setDictShowWay("0"); // 设置字典显示方式：唯一标识
        }
    }

    private static void setFileUploadOptions(String columnName, GeneratorTableColumn tableColumn) {
        String javaType = tableColumn.getJavaType();
        if (arraysContains(GeneratorConstants.AUTO_FILE_UPLOAD_COLUMNS, columnName) || GeneratorConstants.TYPE_LIST_UPLOADRESULT.equals(javaType)) {
            tableColumn.setJavaType(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
            tableColumn.setJavaTypePackage(
                    "com.mybatisflex.core.handler.JacksonTypeHandler,com.sz.core.common.entity.UploadResult,java.util.List,com.mybatisflex.annotation.Column");
            tableColumn.setHtmlType(GeneratorConstants.HTML_FILE_UPLOAD);
            Map<String, Object> options = tableColumn.getOptions() != null ? tableColumn.getOptions() : HashMap.newHashMap(8);
            options.put("upload-files.dir", "tmp"); // 多文件上传组件：文件上传目录
            options.put("upload-files.accept", ""); // 多文件上传组件：允许上传的文件类型, doc,pdf,jpg等,空表示不限制
            options.put("upload-files.limit", 5); // 多文件上传组件：最多上传5个文件
            options.put("upload-files.fileSize", 3); // 多文件上传组件：单个文件最大3MB

            options.put("file-download-list.align", "left"); // 列表文件回显组件：对齐方式left/center/right
            options.put("file-download-list.maxRows", 3); // 列表文件回显组件：表格内最大显示行数,超出则折叠显示
            tableColumn.setOptions(options);
        }
    }

    private static void setJoditEditorOptions(String columnName, GeneratorTableColumn tableColumn) {
        String htmlType = tableColumn.getHtmlType();
        if (GeneratorConstants.HTML_EDITOR.equals(htmlType)) {
            Map<String, Object> options = tableColumn.getOptions() != null ? tableColumn.getOptions() : HashMap.newHashMap(8);
            options.put("uploader.dir", "editor"); // 富文本编辑器：文件上传目录
            options.put("height", "400px"); // 富文本编辑器：编辑器高度
            tableColumn.setOptions(options);
        }
    }

}
