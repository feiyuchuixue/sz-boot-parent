package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysDeptLeader;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 部门领导人表 Service
 * </p>
 *
 * @author sz
 * @since 2024-03-26
 */
public interface SysDeptLeaderService extends IService<SysDeptLeader> {

    @Transactional
    void syncLeader(Long deptId, List<Long> leaderIds);
}