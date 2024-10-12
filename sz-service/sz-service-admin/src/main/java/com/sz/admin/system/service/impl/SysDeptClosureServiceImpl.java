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
import java.util.List;

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
        SysDeptClosure one = getById(nodeId);
        CommonResponseEnum.INVALID_ID.assertNull(one);
        List<SysDeptClosure> desList = descendants(nodeId);
        for (SysDeptClosure closure : desList) {
            QueryWrapper removeWrapper = QueryWrapper.create().eq(SysDeptClosure::getAncestorId, closure.getAncestorId()).eq(SysDeptClosure::getDescendantId,
                    closure.getDescendantId());
            remove(removeWrapper);
        }
        removeById(nodeId);
    }

    /**
     * 查询所有子孙节点
     *
     * @param nodeId
     * @return
     */
    public List<SysDeptClosure> descendants(Long nodeId) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DEPT_CLOSURE.ANCESTOR_ID.eq(nodeId)).where(SYS_DEPT_CLOSURE.DESCENDANT_ID.ne(nodeId));
        return list(wrapper);
    }

    /**
     * 查询指定祖籍节点的所有子孙节点
     *
     * @param ancestorIds
     * @return
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
     * 移动子树
     */
    @Override
    public void move(Long nodeId, Long newNodeId) {
        if (nodeId == newNodeId)
            return;
        List<SysDeptClosure> closures = this.mapper.selectDetachTree(nodeId);
        for (SysDeptClosure closure : closures) {
            QueryWrapper removeWrapper = QueryWrapper.create().eq(SysDeptClosure::getAncestorId, closure.getAncestorId()).eq(SysDeptClosure::getDescendantId,
                    closure.getDescendantId());
            remove(removeWrapper);
        }
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