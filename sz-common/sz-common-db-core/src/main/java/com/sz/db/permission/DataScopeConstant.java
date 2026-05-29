package com.sz.db.permission;

/**
 * 数据权限范围常量 对应业务字典 data_scope（type_code = "data_scope"）
 * <p>
 * 本类定义在 sz-common-db-core 技术层，供数据权限 SQL 拦截器使用。 sz-module-common 中的
 * DataScopeConstant 与本类值完全一致，各自独立维护。
 * <p>
 * 优先级（宽松程度）：ALL > DEPT_AND_BELOW > DEPT_ONLY > SELF_ONLY；CUSTOM 由关联表配置决定具体范围。
 */
public final class DataScopeConstant {

    private DataScopeConstant() {
    }

    /** 全部 */
    public static final String ALL = "1006001";

    /** 本部门及以下 */
    public static final String DEPT_AND_BELOW = "1006002";

    /** 仅本部门 */
    public static final String DEPT_ONLY = "1006003";

    /** 仅本人 */
    public static final String SELF_ONLY = "1006004";

    /** 自定义（具体范围由 sys_data_role_relation 表配置） */
    public static final String CUSTOM = "1006005";
}
