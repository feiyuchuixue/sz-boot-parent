package com.sz.core.common.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum YesNoEnum {

    YES("T", "是"), NO("F", "否");

    private final String code;

    @Getter
    private final String desc;

    @EnumValue
    public String getCode() {
        return code;
    }

}
