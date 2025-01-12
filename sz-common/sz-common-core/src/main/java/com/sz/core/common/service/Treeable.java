package com.sz.core.common.service;

import java.util.List;

/**
 * @author sz
 * @since 2024/3/22 17:34
 */
public interface Treeable<T> {

    Object getId();

    Object getPid();

    Long getDeep();

    Long getSort();

    List<T> getChildren();

    void setChildren(List<T> children);
}
