package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDeptLeaderMapper;
import com.sz.admin.system.pojo.po.SysDeptLeader;
import com.sz.admin.system.service.SysDeptLeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 部门领导人表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-03-26
 */
@Service
@RequiredArgsConstructor
public class SysDeptLeaderServiceImpl extends ServiceImpl<SysDeptLeaderMapper, SysDeptLeader> implements SysDeptLeaderService {

    @Override
    public void syncLeader(Long deptId, List<Long> leaderIds) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysDeptLeader::getDeptId, deptId);
        remove(wrapper); // 清除旧的信息
        List<SysDeptLeader> deptLeaders = new ArrayList<>();
        for (Long leaderId : leaderIds) {
            SysDeptLeader deptLeader = new SysDeptLeader();
            deptLeader.setDeptId(deptId);
            deptLeader.setLeaderId(leaderId);
            deptLeaders.add(deptLeader);
        }
        saveBatch(deptLeaders); // 批量生成新的信息
    }

}