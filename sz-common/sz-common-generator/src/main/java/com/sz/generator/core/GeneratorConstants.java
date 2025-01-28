package com.sz.generator.core;

import java.io.File;

/**
 * 代码生成通用常量
 *
 * @author sz
 */
public class GeneratorConstants {

    private GeneratorConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String PROJECT_JAVA_PREFIX = "src" + File.separator + "main" + File.separator + "java";

    public static final String PROJECT_XML_PREFIX = "src" + File.separator + "main" + File.separator + "resources";

    public static final String PROJECT_WEB_PREFIX = "src";

    /**
     * 单表（增删改查）
     */
    public static final String TPL_CRUD = "crud";

    /**
     * 不需要插入的字段
     */
    public static final String[] NON_INSERTABLE_COLUMNS = {"create_id", "create_time", "del_flag", "update_id", "update_time", "delete_id", "delete_time"};

    /**
     * 页面不需要编辑字段
     */
    public static final String[] NON_EDITABLE_COLUMNS = {"id", "create_id", "create_time", "del_flag", "update_id", "update_time", "delete_id", "delete_time"};

    /**
     * 页面不需要显示的列表字段
     */
    public static final String[] NON_DISPLAYABLE_COLUMNS = {"create_id", "create_time", "del_flag", "update_id", "update_time", "delete_id", "delete_time"};

    /**
     * 页面不需要查询字段
     */
    public static final String[] NON_QUERYABLE_COLUMNS = {"create_id", "create_time", "del_flag", "update_id", "update_time", "remark", "delete_id",
            "delete_time"};

    /**
     * 根据insert事件自动填充的字段
     */
    public static final String[] AUTO_FILL_ON_INSERT_COLUMNS = {"create_id", "create_time"};

    /**
     * 根据update事件自动填充的字段
     */
    public static final String[] AUTO_FILL_ON_UPDATE_COLUMNS = {"update_id", "update_time"};

    /**
     * 文本框
     */
    public static final String HTML_INPUT = "input";

    public static final String HTML_INPUT_NUMBER = "input-number";

    /**
     * 文本域
     */
    public static final String HTML_TEXTAREA = "textarea";

    /**
     * 下拉框
     */
    public static final String HTML_SELECT = "select";

    /**
     * 单选框
     */
    public static final String HTML_RADIO = "radio";

    /**
     * 复选框
     */
    public static final String HTML_CHECKBOX = "checkbox";

    /**
     * 日期控件
     */
    public static final String HTML_DATETIME = "datetime";

    public static final String HTML_DATE = "date";

    public static final String HTML_TIME = "time";

    /**
     * 图片上传控件
     */
    public static final String HTML_IMAGE_UPLOAD = "imageUpload";

    /**
     * 文件上传控件
     */
    public static final String HTML_FILE_UPLOAD = "fileUpload";

    /**
     * 富文本控件
     */
    public static final String HTML_EDITOR = "editor";

    /**
     * 日期范围控件
     */
    public static final String HTML_DATE_PICKER = "date-picker";

    /**
     * 时间范围控件
     */
    public static final String HTML_TIME_PICKER = "time-picker";

    /**
     * 字符串类型
     */
    public static final String TYPE_STRING = "String";

    /**
     * 整型
     */
    public static final String TYPE_INTEGER = "Integer";

    /**
     * ts字符型
     */
    public static final String TS_TYPE_STRING = "string";

    /**
     * ts数字型
     */
    public static final String TS_TYPE_NUMBER = "number";

    /**
     * 长整型
     */
    public static final String TYPE_LONG = "Long";

    /**
     * 浮点型
     */
    public static final String TYPE_DOUBLE = "Double";

    /**
     * 高精度计算类型
     */
    public static final String TYPE_BIG_DECIMAL = "BigDecimal";

    /**
     * 时间类型
     */
    public static final String TYPE_DATE = "Date";

    /**
     * 时间类型
     */
    public static final String TYPE_LOCAL_DATETIME = "LocalDateTime";

    /**
     * 时间类型
     */
    public static final String TYPE_LOCAL_DATE = "LocalDate";

    /**
     * 时间类型
     */
    public static final String TYPE_LOCALTIME = "LocalTime";

    /**
     * 模糊查询
     */
    public static final String QUERY_LIKE = "LIKE";

    /**
     * 相等查询
     */
    public static final String QUERY_EQ = "EQ";

    /**
     * 不等于
     */
    public static final String QUERY_NEQ = "NEQ";

    /**
     * 大于
     */
    public static final String QUERY_GT = "GT";

    /**
     * 小于
     */
    public static final String QUERY_LT = "LT";

    /**
     * 范围
     */
    public static final String QUERY_BETWEEN = "BETWEEN";

    /**
     * 大于等于
     */
    public static final String QUERY_GTE = "GTE";

    /**
     * 小于等于
     */
    public static final String QUERY_LTE = "LTE";

    /**
     * 需要
     */
    public static final String REQUIRE = "1";

    public static final String NOT_REQUIRE = "0";
}
