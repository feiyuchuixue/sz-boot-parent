package com.sz.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName DataScopeEnum
 * @Author sz
 * @Date 2024/6/20 9:56
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    /**
     * "若未指定数据权限，默认具有全部数据权限。"
     */
    ALL("0", "全部数据"),


    /**
     * "在部门管理中，‘本部’指的是用户所属的部门配置。例如，张三作为财务部和研发部的成员，他的‘仅本部’数据权限将包括这两个部门的相关数据"
     */
    DEPT("1", "仅本部门数据"),

    /**
     * ‘本部门及以下数据’ 权限允许用户访问其直属部门以及所有下属部门的数据。例如，如果张三属于市场部，他将能够访问市场部及其所有子部门（如市场研究小组、广告小组等）的数据。
     */
    DEPT_TREE("2", "本部门及以下数据"),

    /**
     * 仅本人创建的数据
     */
    MYSELF("3", "仅本人数据"),

    /**
     * 结合自定义数据配置，根据实际需求灵活配置。
     */
    CUSTOM("4", "自定义数据");

    private final String code;

    private final String name;

    public static DataScopeEnum findCode(String code) {
        for (DataScopeEnum dataScopeEnum : DataScopeEnum.values()) {
            if (dataScopeEnum.getCode().equals(code)) {
                return dataScopeEnum;
            }
        }
        return null;
    }

}
