package com.sz.excel.annotation;

import java.lang.annotation.*;

/**
 * excel 单元格合并(针对列相同项)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CellMerge {

    /**
     * col index
     */
    int index() default -1;

}
