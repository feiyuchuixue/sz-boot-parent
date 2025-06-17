package com.sz.ssoadmin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.ssoadmin.system.mapper.SysDataRoleRelationMapper;
import com.sz.ssoadmin.system.pojo.po.SysDataRoleRelation;
import com.sz.ssoadmin.system.service.SysDataRoleRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.sz.ssoadmin.system.pojo.po.table.SysDataRoleRelationTableDef.SYS_DATA_ROLE_RELATION;

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
    public void batchSave(Long roleId, String relationTypeCd, List<Long> relationIds) {
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

}