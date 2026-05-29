package com.sz.platform.constant.dict;

/**
 * 数据权限关联类型字典常量 对应 sys_dict_type.type_code = "data_scope_relation_type"
 * <p>
 * 注意：初始化数据中 1007001=「部门权限」，1007002=「个人权限」。 部分旧代码注释中存在「用户维度/部门维度」的反标写法，以本常量命名为准。
 */
public final class DataScopeRelationTypeConstant {

    private DataScopeRelationTypeConstant() {
    }

    /** 部门权限 */
    public static final String DEPT = "1007001";

    /** 个人权限 */
    public static final String USER = "1007002";
}
