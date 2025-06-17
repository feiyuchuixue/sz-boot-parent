package com.sz.ssoadmin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.ssoadmin.system.pojo.po.SysDataRoleRelation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 系统数据角色-关联表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-11
 */
public interface SysDataRoleRelationService extends IService<SysDataRoleRelation> {

    @Transactional
    void batchSave(Long roleId, String relationTypeCd, List<Long> relationIds);

    List<Long> getSelectRelationId(Long roleId, String relationTypeCd);
}
