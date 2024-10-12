package com.sz.core.common.service;

import java.util.List;

/**
 * @ClassName Treeable
 * @Author sz
 * @Date 2024/3/22 17:34
 * @Version 1.0
 */
public interface Treeable<T> {

    Object getId();

    Object getPid();

    Long getDeep();

    Long getSort();

    List<T> getChildren();

    void setChildren(List<T> children);
}
