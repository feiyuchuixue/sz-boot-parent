package com.sz.applet.miniBusiness.pojo.po.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SchoolUserBindingTableDef extends TableDef {

    /**
     * <p>
     * 学校用户绑定小程序用户
     * </p>
     *
     * @author sz
     * @since 2025-10-13
     */
    public static final SchoolUserBindingTableDef SCHOOL_USER_BINDING = new SchoolUserBindingTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn SCHOOL_USER_ID = new QueryColumn(this, "school_user_id");

    public final QueryColumn MINI_USER_ID = new QueryColumn(this, "mini_user_id");

    public final QueryColumn BIND_TYPE = new QueryColumn(this, "bind_type");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn DEL_FLAG = new QueryColumn(this, "del_flag");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn CREATE_ID = new QueryColumn(this, "create_id");

    public final QueryColumn UPDATE_ID = new QueryColumn(this, "update_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, SCHOOL_USER_ID, MINI_USER_ID, BIND_TYPE, STATUS, DEL_FLAG, CREATE_TIME, UPDATE_TIME, CREATE_ID, UPDATE_ID};

    public SchoolUserBindingTableDef() {
        super("", "school_user_binding");
    }

    private SchoolUserBindingTableDef(String schema, String name, String alias) {
        super(schema, name, alias);
    }

    public SchoolUserBindingTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SchoolUserBindingTableDef("", "school_user_binding", alias));
    }

}