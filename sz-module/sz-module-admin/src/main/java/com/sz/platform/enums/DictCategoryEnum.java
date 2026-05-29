package com.sz.platform.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典类型分类枚举：system（系统内置）/ business（业务字典）
 * <p>
 * 对应 sys_dict_type.type 字段（VARCHAR(16)）
 * </p>
 */
@Getter
@AllArgsConstructor
public enum DictCategoryEnum {

    SYSTEM("system", "系统字典"), BUSINESS("business", "业务字典");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

}
