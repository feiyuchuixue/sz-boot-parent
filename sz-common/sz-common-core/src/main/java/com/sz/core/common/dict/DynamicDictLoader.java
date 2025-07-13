package com.sz.core.common.dict;

/**
 * 动态字典loader
 */
public interface DynamicDictLoader extends DictLoader {

    /**
     * 返回动态 typeCode（不含前缀）
     */
    String getTypeCode();

    /**
     * 返回动态 typeName
     */
    String getTypeName();

    /**
     * 返回动态前缀
     */
    default String getDynamicPrefix() {
        return "dynamic_";
    }

    /**
     * 返回完整的动态 typeCode（含前缀）
     */
    default String getDynamicTypeCode() {
        return getDynamicPrefix() + getTypeCode();
    }
}
