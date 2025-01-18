package com.sz.core.common.exception.common;

import com.sz.core.common.enums.ErrorPrefixEnum;

public interface IResponseEnum {

    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    int getCode();

    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    String getMessage();

    /**
     * 获取错误码前缀
     * 
     * @return 错误码前缀
     */
    ErrorPrefixEnum getCodePrefixEnum();

}
