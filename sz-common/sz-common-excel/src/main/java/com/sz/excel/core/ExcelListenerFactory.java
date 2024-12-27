package com.sz.excel.core;

import org.springframework.stereotype.Component;

/**
 * @ClassName ExcelListenerFactory
 * @Author sz
 * @Date 2024/12/27 16:12
 * @Version 1.0
 */
@Component
public class ExcelListenerFactory {

    public <T> DefaultExcelListener<T> createListener(boolean validateHeader, Class<T> clazz) {
        return new DefaultExcelListener<>(validateHeader, clazz);
    }
}
