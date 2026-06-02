package com.sz.generator.core.smart;

import com.sz.generator.core.GeneratorConstants;
import com.sz.generator.pojo.po.GeneratorTableColumn;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * 代码生成字段智能推断规则。
 */
public final class GeneratorColumnSmartRules {

    private static final String INSERT_FILL = "FieldFill.INSERT";
    private static final String UPDATE_FILL = "FieldFill.UPDATE";
    private static final String INSERT_UPDATE_FILL = "FieldFill.INSERT_UPDATE";

    private static final String[] HIGH_VALUE_QUERY_SUFFIXES = {"name", "title", "no", "code", "status", "type"};
    private static final String[] DICTIONARY_KEYWORDS = {"status", "type", "sex", "_cd"};
    private static final String[] UPLOAD_KEYWORDS = {"file", "image", "avatar", "photo", "icon", "cover", "attachment", "attachments", "url", "urls"};
    private static final String[] AUDIT_AND_SCOPE_COLUMNS = {"create_id", "create_time", "update_id", "update_time", "delete_id", "delete_time", "dept_scope"};
    private static final String[] LOGIC_DELETE_COLUMNS = {"del_flag", "is_deleted"};

    private GeneratorColumnSmartRules() {
        throw new IllegalStateException("Utility class");
    }

    public static void applyImportDefaults(GeneratorTableColumn column) {
        String columnName = column.getColumnName();
        String columnType = StringUtils.defaultIfBlank(column.getColumnType(), "varchar(255)");
        String dictType = column.getDictType();
        String dictShowWay = column.getDictShowWay();
        column.setColumnType(columnType);
        String dataType = getDbType(columnType);
        defaultSwitches(column);
        column.setDictType(StringUtils.defaultString(dictType));
        column.setDictShowWay(StringUtils.defaultString(dictShowWay));
        setHtmlAndJavaType(dataType, column.getColumnType(), column);
        forceSpecialJavaType(columnName, column);
        setDictionaryHtmlTypeByMatchedDict(columnName, column);
        setLogicDelete(columnName, column);
        setHtmlTypeByName(columnName, column);
        setAutofillType(columnName, column);
        setFillOptions(columnName, column);
        setFileUploadOptions(columnName, column);
        setJoditEditorOptions(column);
        setOperationDefaults(columnName, column);
    }

    public static void normalizeByHtmlType(GeneratorTableColumn column) {
        String htmlType = column.getHtmlType();
        if ("radio-group".equals(htmlType)) {
            column.setHtmlType(GeneratorConstants.HTML_RADIO);
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_RADIO.equals(htmlType)) {
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_SELECT.equals(htmlType) || GeneratorConstants.HTML_CHECKBOX.equals(htmlType)) {
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (isUploadHtmlType(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
            column.setJavaTypePackage("com.sz.db.handler.Jackson3TypeHandler,com.sz.resource.model.ResourceRef,java.util.List,com.mybatisflex.annotation.Column");
            column.setSearchType("input");
            return;
        }
        if (GeneratorConstants.HTML_DATE.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATE);
            column.setJavaTypePackage("java.time.LocalDate");
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
            return;
        }
        if (GeneratorConstants.HTML_TIME.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCALTIME);
            column.setJavaTypePackage("java.time.LocalTime");
            column.setSearchType(GeneratorConstants.HTML_TIME_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
            return;
        }
        if (GeneratorConstants.HTML_DATETIME.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATETIME);
            column.setJavaTypePackage("java.time.LocalDateTime");
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
        }
    }

    public static void normalizeByHtmlType(GeneratorDetailVO.Column column) {
        String htmlType = column.getHtmlType();
        if ("radio-group".equals(htmlType)) {
            column.setHtmlType(GeneratorConstants.HTML_RADIO);
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_RADIO.equals(htmlType)) {
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_SELECT.equals(htmlType) || GeneratorConstants.HTML_CHECKBOX.equals(htmlType)) {
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (isUploadHtmlType(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
            column.setJavaTypePackage("com.sz.db.handler.Jackson3TypeHandler,com.sz.resource.model.ResourceRef,java.util.List,com.mybatisflex.annotation.Column");
            column.setSearchType("input");
            return;
        }
        if (GeneratorConstants.HTML_DATE.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATE);
            column.setJavaTypePackage("java.time.LocalDate");
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
            return;
        }
        if (GeneratorConstants.HTML_TIME.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCALTIME);
            column.setJavaTypePackage("java.time.LocalTime");
            column.setSearchType(GeneratorConstants.HTML_TIME_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
            return;
        }
        if (GeneratorConstants.HTML_DATETIME.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATETIME);
            column.setJavaTypePackage("java.time.LocalDateTime");
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
        }
    }

    public static boolean isDictionaryDisplayHtmlType(String htmlType) {
        return GeneratorConstants.HTML_SELECT.equals(htmlType) || GeneratorConstants.HTML_RADIO.equals(htmlType)
                || GeneratorConstants.HTML_CHECKBOX.equals(htmlType) || "radio-group".equals(htmlType);
    }

    public static List<GeneratorDetailVO.SmartHint> buildSmartHints(GeneratorDetailVO.Column column) {
        List<GeneratorDetailVO.SmartHint> hints = new ArrayList<>();
        if (GeneratorConstants.REQUIRE.equals(column.getIsLogicDel())) {
            hints.add(hint("info", "逻辑删除", "逻辑删除字段通常不参与页面展示、查询、导入和导出。"));
            if (!isStringCompatibleDbType(column.getColumnType())) {
                hints.add(hint("danger", "类型不匹配", "当前项目逻辑删除配置使用 T/F，数据库字段建议改为 varchar/char；数字类型会导致逻辑删除值不匹配。"));
            }
        }
        if (isDictionaryDisplayHtmlType(column.getHtmlType()) && StringUtils.isBlank(column.getDictType())) {
            hints.add(hint("danger", "字典缺失", "当前显示类型依赖字典数据，请选择字典类型后再进入下一步。"));
        } else if (isPotentialDictionaryColumn(column.getColumnName()) && StringUtils.isBlank(column.getDictType())) {
            hints.add(hint("warning", "建议字典", "该字段疑似枚举/字典字段；未匹配到字典时将按普通输入生成，页面仍可直接运行。"));
        }
        if (isUploadColumn(column.getColumnName()) || isUploadHtmlType(column.getHtmlType())
                || GeneratorConstants.TYPE_LIST_UPLOADRESULT.equals(column.getJavaType())) {
            hints.add(hint("warning", "上传字段", "已按上传字段处理，请生成后确认资源场景 sceneCode 与业务目录是否需要调整。"));
        }
        if (GeneratorConstants.REQUIRE.equals(column.getIsAutofill())) {
            hints.add(hint("info", "自动填充", autofillDescription(column.getAutofillType())));
        }
        if ("dept_scope".equals(column.getColumnName())) {
            hints.add(hint("info", "数据权限", "该字段通常由数据权限逻辑维护，不建议作为普通表单、查询、导入或导出字段。"));
        }
        if ("create_id".equals(column.getColumnName()) || "update_id".equals(column.getColumnName())) {
            hints.add(hint("info", "权限字段", "该字段通常用于创建人/更新人展示和权限判断，默认不参与表单或查询。"));
        }
        return hints;
    }

    private static void defaultSwitches(GeneratorTableColumn column) {
        column.setJavaType(GeneratorConstants.TYPE_STRING);
        column.setTsType(GeneratorConstants.TS_TYPE_STRING);
        column.setJavaTypePackage("");
        column.setSearchType("input");
        column.setQueryType(GeneratorConstants.QUERY_EQ);
        column.setDictType("");
        column.setDictShowWay("");
        column.setAutofillType("");
        column.setIsLogicDel(GeneratorConstants.NOT_REQUIRE);
        column.setIsAutofill(GeneratorConstants.NOT_REQUIRE);
        column.setIsUniqueValid(GeneratorConstants.NOT_REQUIRE);
        column.setIsInsert(GeneratorConstants.NOT_REQUIRE);
        column.setIsEdit(GeneratorConstants.NOT_REQUIRE);
        column.setIsList(GeneratorConstants.NOT_REQUIRE);
        column.setIsQuery(GeneratorConstants.NOT_REQUIRE);
        column.setIsImport(GeneratorConstants.NOT_REQUIRE);
        column.setIsExport(GeneratorConstants.NOT_REQUIRE);
        column.setIsRequired(defaultValue(column.getIsRequired()));
    }

    private static void setHtmlAndJavaType(String dataType, String columnType, GeneratorTableColumn column) {
        switch (dataType.toUpperCase()) {
            case "VARCHAR" :
            case "CHAR" :
            case "NVARCHAR" :
            case "VARCHAR2" :
            case "TEXT" :
            case "TINYTEXT" :
            case "MEDIUMTEXT" :
            case "LONGTEXT" :
            case "JSON" :
            case "JSONB" :
                setStringTypeAttributes(columnType, column);
                break;
            case "TIME" :
                column.setJavaType(GeneratorConstants.TYPE_LOCALTIME);
                column.setHtmlType(GeneratorConstants.HTML_TIME);
                column.setJavaTypePackage("java.time.LocalTime");
                column.setSearchType(GeneratorConstants.HTML_TIME_PICKER);
                column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
                break;
            case "DATE" :
                column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATE);
                column.setHtmlType(GeneratorConstants.HTML_DATE);
                column.setJavaTypePackage("java.time.LocalDate");
                column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
                column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
                break;
            case "TIMESTAMP" :
            case "DATETIME" :
                column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATETIME);
                column.setHtmlType(GeneratorConstants.HTML_DATETIME);
                column.setJavaTypePackage("java.time.LocalDateTime");
                column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
                column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
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
            case "NUMERIC" :
                column.setTsType(GeneratorConstants.TS_TYPE_NUMBER);
                setNumberTypeAttributes(columnType, column);
                break;
            default :
                column.setHtmlType(GeneratorConstants.HTML_INPUT);
        }
    }

    private static void setStringTypeAttributes(String columnType, GeneratorTableColumn column) {
        int columnLength = getColumnLength(columnType);
        String htmlType = columnLength >= 500 ? GeneratorConstants.HTML_TEXTAREA : GeneratorConstants.HTML_INPUT;
        column.setHtmlType(htmlType);
    }

    private static void setNumberTypeAttributes(String columnType, GeneratorTableColumn column) {
        String dbType = getDbType(columnType).toUpperCase();
        if ("DECIMAL".equals(dbType) || "NUMERIC".equals(dbType)) {
            column.setJavaType(GeneratorConstants.TYPE_BIG_DECIMAL);
            column.setJavaTypePackage("java.math.BigDecimal");
            column.setHtmlType(GeneratorConstants.HTML_INPUT_NUMBER);
            return;
        }
        String[] parts = StringUtils.split(substringBetween(columnType, "(", ")"), ",");
        if (parts != null && parts.length == 2 && Integer.parseInt(parts[1].trim()) > 0) {
            column.setJavaType(GeneratorConstants.TYPE_BIG_DECIMAL);
            column.setJavaTypePackage("java.math.BigDecimal");
        } else if (parts != null && parts.length == 1 && Integer.parseInt(parts[0].trim()) > 10) {
            column.setJavaType(GeneratorConstants.TYPE_LONG);
        } else {
            column.setJavaType(GeneratorConstants.TYPE_INTEGER);
        }
        column.setHtmlType(GeneratorConstants.HTML_INPUT_NUMBER);
    }

    private static void forceSpecialJavaType(String columnName, GeneratorTableColumn column) {
        String dbType = getDbType(column.getColumnType()).toLowerCase();
        if (isPrimaryKey(column) && ("int".equals(dbType) || "bigint".equals(dbType))) {
            column.setJavaType(GeneratorConstants.TYPE_LONG);
        }
        if ("bigint".equals(dbType)) {
            column.setJavaType(GeneratorConstants.TYPE_LONG);
        }
        if ("create_id".equals(columnName) || "update_id".equals(columnName) || "delete_id".equals(columnName)) {
            column.setJavaType(GeneratorConstants.TYPE_LONG);
        }
    }

    private static void setLogicDelete(String columnName, GeneratorTableColumn column) {
        if (!isLogicDeleteColumn(columnName)) {
            return;
        }
        column.setIsLogicDel(GeneratorConstants.REQUIRE);
        column.setJavaType(GeneratorConstants.TYPE_STRING);
        column.setTsType(GeneratorConstants.TS_TYPE_STRING);
        column.setJavaTypePackage("");
        column.setHtmlType(GeneratorConstants.HTML_INPUT);
        column.setSearchType(GeneratorConstants.HTML_INPUT);
        column.setQueryType(GeneratorConstants.QUERY_EQ);
    }

    private static void setDictionaryHtmlTypeByMatchedDict(String columnName, GeneratorTableColumn column) {
        if (StringUtils.isBlank(column.getDictType()) || isUploadHtmlType(column.getHtmlType()) || !isPotentialDictionaryColumn(columnName)) {
            return;
        }
        String lowerName = columnName.toLowerCase();
        column.setHtmlType(lowerName.contains("status") ? GeneratorConstants.HTML_RADIO : GeneratorConstants.HTML_SELECT);
        column.setSearchType("select");
        defaultDictShowWay(column);
    }

    private static void setHtmlTypeByName(String columnName, GeneratorTableColumn column) {
        String lowerName = columnName.toLowerCase();
        if (isImageColumn(lowerName)) {
            column.setHtmlType(GeneratorConstants.HTML_IMAGE_UPLOAD);
            return;
        }
        if (isUploadColumn(lowerName)) {
            column.setHtmlType(GeneratorConstants.HTML_FILE_UPLOAD);
            return;
        }
        if (lowerName.contains("content")) {
            column.setHtmlType(GeneratorConstants.HTML_EDITOR);
        } else if (lowerName.contains("time") && GeneratorConstants.HTML_INPUT.equals(column.getSearchType())) {
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
        }
    }

    private static void setOperationDefaults(String columnName, GeneratorTableColumn column) {
        boolean notPk = !isPrimaryKey(column);
        boolean notLogicDelete = !GeneratorConstants.REQUIRE.equals(column.getIsLogicDel());
        if (notPk && notLogicDelete && !contains(GeneratorConstants.NON_INSERTABLE_COLUMNS, columnName) && !isAutofillColumn(columnName)) {
            column.setIsInsert(GeneratorConstants.REQUIRE);
        }
        if (notPk && notLogicDelete && !contains(GeneratorConstants.NON_EDITABLE_COLUMNS, columnName) && !isAutofillColumn(columnName)) {
            column.setIsEdit(GeneratorConstants.REQUIRE);
        }
        if (notLogicDelete && !contains(GeneratorConstants.NON_DISPLAYABLE_COLUMNS, columnName)) {
            column.setIsList(GeneratorConstants.REQUIRE);
        }
        if (shouldQuery(columnName, column)) {
            column.setIsQuery(GeneratorConstants.REQUIRE);
        }
        if (shouldImport(columnName, column)) {
            column.setIsImport(GeneratorConstants.REQUIRE);
        }
        if (shouldExport(columnName, column)) {
            column.setIsExport(GeneratorConstants.REQUIRE);
        }
        setQueryType(columnName, column);
    }

    private static boolean shouldQuery(String columnName, GeneratorTableColumn column) {
        if (isPrimaryKey(column) || contains(GeneratorConstants.NON_QUERYABLE_COLUMNS, columnName) || isUploadHtmlType(column.getHtmlType())) {
            return false;
        }
        if (GeneratorConstants.REQUIRE.equals(column.getIsLogicDel())) {
            return false;
        }
        if (GeneratorConstants.QUERY_BETWEEN.equals(column.getQueryType())) {
            return true;
        }
        String lowerName = columnName.toLowerCase();
        if (lowerName.endsWith("_id") && !contains(AUDIT_AND_SCOPE_COLUMNS, lowerName)) {
            return true;
        }
        return Arrays.stream(HIGH_VALUE_QUERY_SUFFIXES).anyMatch(item -> lowerName.endsWith(item) || lowerName.contains("_" + item + "_"));
    }

    private static boolean shouldImport(String columnName, GeneratorTableColumn column) {
        return !isPrimaryKey(column) && !contains(GeneratorConstants.NON_DISPLAYABLE_IMPORT_COLUMNS, columnName)
                && !GeneratorConstants.HTML_EDITOR.equals(column.getHtmlType()) && !isUploadHtmlType(column.getHtmlType())
                && !isAutofillColumn(columnName) && !GeneratorConstants.REQUIRE.equals(column.getIsLogicDel());
    }

    private static boolean shouldExport(String columnName, GeneratorTableColumn column) {
        return !isPrimaryKey(column) && !contains(GeneratorConstants.NON_EXPORTABLE_COLUMNS, columnName)
                && !GeneratorConstants.HTML_EDITOR.equals(column.getHtmlType()) && !isUploadHtmlType(column.getHtmlType())
                && !GeneratorConstants.REQUIRE.equals(column.getIsLogicDel());
    }

    private static void setQueryType(String columnName, GeneratorTableColumn column) {
        String lowerName = columnName.toLowerCase();
        if (Strings.CI.endsWith(lowerName, "name") || Strings.CI.endsWith(lowerName, "title")) {
            column.setQueryType(GeneratorConstants.QUERY_LIKE);
        }
    }

    private static void setAutofillType(String columnName, GeneratorTableColumn column) {
        if (contains(GeneratorConstants.AUTO_FILL_ON_INSERT_COLUMNS, columnName)) {
            column.setIsAutofill(GeneratorConstants.REQUIRE);
            column.setAutofillType(INSERT_FILL);
        }
        if (contains(GeneratorConstants.AUTO_FILL_ON_UPDATE_COLUMNS, columnName)) {
            column.setIsAutofill(GeneratorConstants.REQUIRE);
            column.setAutofillType(UPDATE_FILL);
        }
    }

    private static void setFillOptions(String columnName, GeneratorTableColumn column) {
        if (contains(GeneratorConstants.AUTO_FILL_OPTIONS_COLUMNS, columnName)) {
            column.setDictType("dynamic_user_options");
            column.setDictShowWay("0");
        }
    }

    private static void setFileUploadOptions(String columnName, GeneratorTableColumn column) {
        String htmlType = column.getHtmlType();
        boolean isUpload = isUploadColumn(columnName) || GeneratorConstants.TYPE_LIST_UPLOADRESULT.equals(column.getJavaType()) || isUploadHtmlType(htmlType);
        if (!isUpload) {
            return;
        }
        column.setJavaType(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
        column.setJavaTypePackage("com.sz.db.handler.Jackson3TypeHandler,com.sz.resource.model.ResourceRef,java.util.List,com.mybatisflex.annotation.Column");
        if (!GeneratorConstants.HTML_IMAGE_UPLOAD.equals(htmlType)) {
            column.setHtmlType(GeneratorConstants.HTML_FILE_UPLOAD);
        }
        Map<String, Object> options = column.getOptions() != null ? column.getOptions() : new HashMap<>();
        options.put("upload-files.sceneCode", "system.temp");
        options.put("upload-files.pathSegments", "your_biz_path");
        options.put("upload-files.accept", GeneratorConstants.HTML_IMAGE_UPLOAD.equals(column.getHtmlType()) ? "image/*" : "");
        options.put("upload-files.limit", GeneratorConstants.HTML_IMAGE_UPLOAD.equals(column.getHtmlType()) ? 1 : 5);
        options.put("upload-files.fileSize", 3);
        options.put("file-download-list.align", "left");
        options.put("file-download-list.maxRows", 3);
        column.setOptions(options);
    }

    private static void setJoditEditorOptions(GeneratorTableColumn column) {
        if (!GeneratorConstants.HTML_EDITOR.equals(column.getHtmlType())) {
            return;
        }
        Map<String, Object> options = column.getOptions() != null ? column.getOptions() : new HashMap<>();
        options.put("upload.sceneCode", "system.temp");
        options.put("upload.pathSegments", "your_editor_biz_path");
        options.put("height", "400px");
        column.setOptions(options);
    }

    private static boolean isPotentialDictionaryColumn(String columnName) {
        String lowerName = columnName == null ? "" : columnName.toLowerCase();
        return Arrays.stream(DICTIONARY_KEYWORDS).anyMatch(lowerName::contains);
    }

    private static boolean isImageColumn(String columnName) {
        String lowerName = columnName == null ? "" : columnName.toLowerCase();
        return lowerName.contains("image") || lowerName.contains("avatar") || lowerName.contains("photo") || lowerName.contains("icon")
                || lowerName.contains("cover");
    }

    private static boolean isUploadColumn(String columnName) {
        String lowerName = columnName == null ? "" : columnName.toLowerCase();
        return Arrays.stream(UPLOAD_KEYWORDS).anyMatch(lowerName::contains);
    }

    private static boolean isUploadHtmlType(String htmlType) {
        return GeneratorConstants.HTML_FILE_UPLOAD.equals(htmlType) || GeneratorConstants.HTML_IMAGE_UPLOAD.equals(htmlType);
    }

    private static boolean isAutofillColumn(String columnName) {
        return contains(GeneratorConstants.AUTO_FILL_ON_INSERT_COLUMNS, columnName) || contains(GeneratorConstants.AUTO_FILL_ON_UPDATE_COLUMNS, columnName);
    }

    private static boolean isPrimaryKey(GeneratorTableColumn column) {
        return GeneratorConstants.REQUIRE.equals(column.getIsPk());
    }

    private static boolean isLogicDeleteColumn(String columnName) {
        return contains(LOGIC_DELETE_COLUMNS, StringUtils.defaultString(columnName).toLowerCase());
    }

    private static boolean isStringCompatibleDbType(String columnType) {
        String dbType = getDbType(columnType).toUpperCase();
        return "VARCHAR".equals(dbType) || "CHAR".equals(dbType) || "NVARCHAR".equals(dbType) || "VARCHAR2".equals(dbType)
                || "TEXT".equals(dbType) || "CHARACTER".equals(dbType) || "CHARACTER VARYING".equals(dbType);
    }

    private static String getDbType(String columnType) {
        String normalized = StringUtils.defaultString(columnType).trim();
        if (Strings.CS.indexOf(normalized, "(") > 0) {
            return substringBefore(normalized, "(");
        }
        return substringBefore(normalized, " ");
    }

    private static int getColumnLength(String columnType) {
        String normalized = StringUtils.defaultString(columnType).trim();
        if (Strings.CS.indexOf(normalized, "(") > 0) {
            String length = substringBetween(normalized, "(", ")");
            String normalizedLength = substringBefore(StringUtils.defaultString(length), ",");
            if (StringUtils.isNumeric(normalizedLength)) {
                return Integer.parseInt(normalizedLength);
            }
        }
        return 0;
    }

    private static boolean contains(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    private static String defaultValue(String value) {
        return StringUtils.defaultIfBlank(value, GeneratorConstants.NOT_REQUIRE);
    }

    private static void defaultDictShowWay(GeneratorTableColumn column) {
        if (StringUtils.isNotBlank(column.getDictType()) && StringUtils.isBlank(column.getDictShowWay())) {
            column.setDictShowWay("0");
        }
    }

    private static void defaultDictShowWay(GeneratorDetailVO.Column column) {
        if (StringUtils.isNotBlank(column.getDictType()) && StringUtils.isBlank(column.getDictShowWay())) {
            column.setDictShowWay("0");
        }
    }

    private static GeneratorDetailVO.SmartHint hint(String type, String label, String message) {
        GeneratorDetailVO.SmartHint hint = new GeneratorDetailVO.SmartHint();
        hint.setType(type);
        hint.setLabel(label);
        hint.setMessage(message);
        return hint;
    }

    private static String autofillDescription(String autofillType) {
        if (INSERT_FILL.equals(autofillType)) {
            return "该字段由后端在新增时自动填充，默认不参与表单录入。";
        }
        if (UPDATE_FILL.equals(autofillType)) {
            return "该字段由后端在更新时自动填充，默认不参与表单录入。";
        }
        if (INSERT_UPDATE_FILL.equals(autofillType)) {
            return "该字段由后端在新增和更新时自动填充，默认不参与表单录入。";
        }
        return "该字段由后端自动填充规则维护。";
    }
}
