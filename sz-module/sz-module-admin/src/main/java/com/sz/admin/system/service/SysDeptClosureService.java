package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysDeptClosure;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门祖籍关系表 Service
 * </p>
 *
 * @author sz
 * @since 2024-03-28
 */
public interface SysDeptClosureService extends IService<SysDeptClosure> {

    List<SysDeptClosure> ancestorsPath(Long deptId);

    void create(Long deptId, Long parentDeptId);

    @Transactional
    void remove(Long nodeId);

    List<Long> descendants(List<Long> ancestorIds);

    /**
     * 批量查询多个祖先节点的子孙节点，按祖先分组返回（单次 IN 查询）
     *
     * @param ancestorIds
     *            祖先节点ID列表
     * @return ancestorId -> 子孙节点ID列表（含自身）的映射
     */
    Map<Long, List<Long>> descendantsGroupByAncestor(List<Long> ancestorIds);

    void move(Long nodeId, Long newNodeId);
}