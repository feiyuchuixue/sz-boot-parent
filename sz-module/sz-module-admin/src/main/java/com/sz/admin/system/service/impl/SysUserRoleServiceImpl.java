package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysUserRoleMapper;
import com.sz.admin.system.pojo.po.SysUserRole;
import com.sz.admin.system.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sz.admin.system.pojo.po.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * <p>
 * 系统用户-角色关联表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    /**
     * 获取用户的角色
     * 
     * @param userId
     *            用户id
     * @return 用户角色集合
     */
    @Override
    public List<String> getUserRolesByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select(SYS_USER_ROLE.ROLE_ID).from(SYS_USER_ROLE).where(SYS_USER_ROLE.USER_ID.eq(userId));
        return listAs(queryWrapper, String.class);
    }

    /**
     * 批量查询多个用户的角色（单次 IN 查询）
     *
     * @param userIds
     *            用户ID列表
     * @return userId -> 角色ID列表 的映射
     */
    @Override
    public Map<Long, List<String>> getUserRolesByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        QueryWrapper queryWrapper = QueryWrapper.create().select(SYS_USER_ROLE.USER_ID, SYS_USER_ROLE.ROLE_ID).from(SYS_USER_ROLE)
                .where(SYS_USER_ROLE.USER_ID.in(userIds));
        List<SysUserRole> list = list(queryWrapper);
        return list.stream()
                .collect(Collectors.groupingBy(SysUserRole::getUserId, Collectors.mapping(r -> String.valueOf(r.getRoleId()), Collectors.toList())));
    }

}