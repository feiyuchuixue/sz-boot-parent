package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysUserRoleMapper;
import com.sz.admin.system.pojo.po.SysUserRole;
import com.sz.admin.system.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * @return
     */
    @Override
    public List<String> getUserRolesByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select(SYS_USER_ROLE.ROLE_ID).from(SYS_USER_ROLE).where(SYS_USER_ROLE.USER_ID.eq(userId));
        List<String> list = listAs(queryWrapper, String.class);
        return list;
    }

}