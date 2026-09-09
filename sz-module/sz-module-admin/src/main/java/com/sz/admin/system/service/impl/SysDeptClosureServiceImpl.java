package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDeptClosureMapper;
import com.sz.admin.system.pojo.po.SysDeptClosure;
import com.sz.admin.system.service.SysDeptClosureService;
import com.sz.core.common.enums.CommonResponseEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sz.admin.system.pojo.po.table.SysDeptClosureTableDef.SYS_DEPT_CLOSURE;

/**
 * <p>
 * 部门祖籍关系表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-03-28
 */
@Service
@RequiredArgsConstructor
public class SysDeptClosureServiceImpl extends ServiceImpl<SysDeptClosureMapper, SysDeptClosure> implements SysDeptClosureService {

    @Override
    public List<SysDeptClosure> ancestorsPath(Long nodeId) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DEPT_CLOSURE.DESCENDANT_ID.eq(nodeId)).where(SYS_DEPT_CLOSURE.ANCESTOR_ID.ne(nodeId))
                .where(SYS_DEPT_CLOSURE.DEPTH.gt(0));
        return list(wrapper);
    }

    @Override
    public void create(Long nodeId, Long pid) {
        List<SysDeptClosure> closures = new ArrayList<>();
        List<SysDeptClosure> pathList = ancestorsPath(pid);
        for (SysDeptClosure path : pathList) {
            SysDeptClosure closure = buildClosure(path.getAncestorId(), nodeId, path.getDepth() + 1);
            closures.add(closure);
        }
        // self
        SysDeptClosure self = buildClosure(nodeId, nodeId, 0);
        // parent
        SysDeptClosure parent = buildClosure(pid, nodeId, 1);
        closures.add(self);
        closures.add(parent);
        saveBatch(closures);
    }

    @Override
    @Transactional
    public void remove(Long nodeId) {
        // 验证节点存在（查自身记录：ancestor_id = descendant_id = nodeId）
        QueryWrapper selfWrapper = QueryWrapper.create().eq(SysDeptClosure::getAncestorId, nodeId).eq(SysDeptClosure::getDescendantId, nodeId);
        SysDeptClosure one = getOne(selfWrapper);
        CommonResponseEnum.INVALID_ID.assertNull(one);
        // 删除所有以 nodeId 子树为后代的记录（ancestor_id 在子树内的所有行）
        List<SysDeptClosure> desList = descendants(nodeId);
        List<Long> subtreeIds = new ArrayList<>();
        subtreeIds.add(nodeId);
        for (SysDeptClosure closure : desList) {
            subtreeIds.add(closure.getDescendantId());
        }
        // 一次性删除子树内所有节点相关的全部闭包记录（含作为后代时的祖先链记录）
        QueryWrapper removeWrapper = QueryWrapper.create().in(SysDeptClosure::getDescendantId, subtreeIds);
        remove(removeWrapper);
    }

    /**
     * 查询所有子孙节点
     *
     * @param nodeId
     *            节点ID
     * @return 子孙节点
     */
    public List<SysDeptClosure> descendants(Long nodeId) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DEPT_CLOSURE.ANCESTOR_ID.eq(nodeId)).where(SYS_DEPT_CLOSURE.DESCENDANT_ID.ne(nodeId));
        return list(wrapper);
    }

    /**
     * 查询指定祖籍节点的所有子孙节点
     *
     * @param ancestorIds
     *            祖籍节点ID
     * @return 子孙节点ID
     */
    @Override
    public List<Long> descendants(List<Long> ancestorIds) {
        if (ancestorIds.isEmpty()) {
            return new ArrayList<>();
        }
        QueryWrapper wrapper = QueryWrapper.create().select(QueryMethods.distinct(SYS_DEPT_CLOSURE.DESCENDANT_ID))
                .where(SYS_DEPT_CLOSURE.ANCESTOR_ID.in(ancestorIds));
        return listAs(wrapper, Long.class);
    }

    /**
     * 批量查询多个祖先节点的子孙节点，按祖先分组返回（单次 IN 查询）
     *
     * @param ancestorIds
     *            祖先节点ID列表
     * @return ancestorId -> 子孙节点ID列表（含自身）的映射
     */
    @Override
    public Map<Long, List<Long>> descendantsGroupByAncestor(List<Long> ancestorIds) {
        if (ancestorIds == null || ancestorIds.isEmpty()) {
            return Collections.emptyMap();
        }
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_DEPT_CLOSURE.ANCESTOR_ID, SYS_DEPT_CLOSURE.DESCENDANT_ID)
                .where(SYS_DEPT_CLOSURE.ANCESTOR_ID.in(ancestorIds));
        List<SysDeptClosure> list = list(wrapper);
        return list.stream()
                .collect(Collectors.groupingBy(SysDeptClosure::getAncestorId, Collectors.mapping(SysDeptClosure::getDescendantId, Collectors.toList())));
    }

    /**
     * 移动子树
     */
    @Override
    public void move(Long nodeId, Long newNodeId) {
        if (Objects.equals(nodeId, newNodeId))
            return;
        // 直接执行批量 detach SQL，避免 Java 循环逐条删除
        this.mapper.detach(nodeId);
        // 嫁接到新父节点
        this.mapper.graft(nodeId, newNodeId);
    }

    private SysDeptClosure buildClosure(Long ancestorId, Long descendantId, Integer depth) {
        SysDeptClosure closure = new SysDeptClosure();
        closure.setAncestorId(ancestorId);
        closure.setDescendantId(descendantId);
        closure.setDepth(depth);
        return closure;
    }

}