package com.sz.applet.miniBusiness.pojo.po.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SchoolUserTableDef extends TableDef {

    /**
     * <p>
     * 学校师生表
     * </p>
     *
     * @author sz
     * @since 2025-10-13
     */
    public static final SchoolUserTableDef SCHOOL_USER = new SchoolUserTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    public final QueryColumn IDENTITY = new QueryColumn(this, "identity");

    public final QueryColumn ID_CARD = new QueryColumn(this, "id_card");

    public final QueryColumn STUDENT_ID = new QueryColumn(this, "student_id");

    public final QueryColumn YEAR = new QueryColumn(this, "year");

    public final QueryColumn CLASS_NO = new QueryColumn(this, "class_no");

    public final QueryColumn TEACHER_ID = new QueryColumn(this, "teacher_id");

    public final QueryColumn DEL_FLAG = new QueryColumn(this, "del_flag");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn CREATE_ID = new QueryColumn(this, "create_id");

    public final QueryColumn UPDATE_ID = new QueryColumn(this, "update_id");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, PHONE, IDENTITY, ID_CARD, STUDENT_ID, YEAR, CLASS_NO, TEACHER_ID, DEL_FLAG, CREATE_TIME, UPDATE_TIME, CREATE_ID, UPDATE_ID, STATUS};

    public SchoolUserTableDef() {
        super("", "school_user");
    }

    private SchoolUserTableDef(String schema, String name, String alias) {
        super(schema, name, alias);
    }

    public SchoolUserTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SchoolUserTableDef("", "school_user", alias));
    }

}