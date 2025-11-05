package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDataRoleRelationMapper;
import com.sz.admin.system.pojo.po.SysDataRoleRelation;
import com.sz.admin.system.service.SysDataRoleRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.sz.admin.system.pojo.po.table.SysDataRoleRelationTableDef.SYS_DATA_ROLE_RELATION;

/**
 * <p>
 * 系统数据角色-关联表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-07-11
 */
@Service
@RequiredArgsConstructor
public class SysDataRoleRelationServiceImpl extends ServiceImpl<SysDataRoleRelationMapper, SysDataRoleRelation> implements SysDataRoleRelationService {

    @Transactional
    @Override
    public void batchSave(Long roleId, String menuId, String relationTypeCd, List<Long> relationIds) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DATA_ROLE_RELATION.ROLE_ID.eq(roleId))
                .where(SYS_DATA_ROLE_RELATION.RELATION_TYPE_CD.eq(relationTypeCd));
        long count = count(wrapper);
        if (count > 0) {
            remove(wrapper);
        }
        List<SysDataRoleRelation> roleRelations = new ArrayList<>();
        SysDataRoleRelation roleRelation;
        for (Long relationId : relationIds) {
            roleRelation = new SysDataRoleRelation();
            roleRelation.setRoleId(roleId);
            roleRelation.setRelationId(relationId);
            roleRelation.setRelationTypeCd(relationTypeCd);
            roleRelation.setMenuId(menuId);
            roleRelations.add(roleRelation);
        }
        if (!roleRelations.isEmpty())
            saveBatch(roleRelations);
    }

    @Override
    public List<Long> getSelectRelationId(Long roleId, String relationTypeCd) {
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_DATA_ROLE_RELATION.RELATION_ID).where(SYS_DATA_ROLE_RELATION.ROLE_ID.eq(roleId))
                .where(SYS_DATA_ROLE_RELATION.RELATION_TYPE_CD.eq(relationTypeCd));
        return listAs(wrapper, Long.class);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DATA_ROLE_RELATION.ROLE_ID.eq(roleId));
        remove(wrapper);
    }

    @Override
    public List<SysDataRoleRelation> queryRelationByRoleIdAndMenuIds(Long roleId, List<String> menuIds) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DATA_ROLE_RELATION.ROLE_ID.eq(roleId)).where(SYS_DATA_ROLE_RELATION.MENU_ID.in(menuIds));
        return list(wrapper);
    }
    @Override
    public List<SysDataRoleRelation> listByRoleIdsAndMenuIds(Collection<String> roleIds, List<String> menuIds) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DATA_ROLE_RELATION.ROLE_ID.in(roleIds)).where(SYS_DATA_ROLE_RELATION.MENU_ID.in(menuIds));
        return list(wrapper);
    }

}