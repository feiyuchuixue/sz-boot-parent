package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDataRoleMapper;
import com.sz.admin.system.pojo.dto.sysmenu.SysUserRoleDTO;
import com.sz.admin.system.pojo.po.SysDataRole;
import com.sz.admin.system.pojo.vo.sysuser.SysUserRoleVO;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.BeanCopyUtils;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.system.service.SysUserDataRoleService;
import com.sz.admin.system.pojo.po.SysUserDataRole;
import com.sz.admin.system.mapper.SysUserDataRoleMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sz.admin.system.pojo.po.table.SysUserDataRoleTableDef.SYS_USER_DATA_ROLE;

/**
 * <p>
 * 系统用户-数据角色关联表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-11
 */
@Service
@RequiredArgsConstructor
public class SysUserDataRoleServiceImpl extends ServiceImpl<SysUserDataRoleMapper, SysUserDataRole> implements SysUserDataRoleService {

    private final SysDataRoleMapper sysDataRoleMapper;

    private final EventPublisher eventPublisher;

    @Transactional
    @Override
    public void changeRole(SysUserRoleDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_USER_DATA_ROLE.USER_ID.eq(dto.getUserId()));
        if (count(wrapper) > 0) {
            remove(wrapper);
        }
        List<SysUserDataRole> dataRoles = new ArrayList<>();
        SysUserDataRole dataRole = null;
        for (Long roleId : dto.getRoleIds()) {
            dataRole = new SysUserDataRole();
            dataRole.setUserId(dto.getUserId());
            dataRole.setRoleId(roleId);
            dataRoles.add(dataRole);
        }
        if (dataRoles.isEmpty())
            return;
        saveBatch(dataRoles);
        Long userId = dto.getUserId();
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(Collections.singletonList(userId)))); // 发送用户元数据change事件

    }

    @Override
    public SysUserRoleVO queryRoleMenu(Long userId) {
        List<SysDataRole> list = QueryChain.of(sysDataRoleMapper).list();
        List<SysUserRoleVO.RoleInfoVO> roleInfoVOS = BeanCopyUtils.copyList(list, SysUserRoleVO.RoleInfoVO.class);
        List<Long> roleIds = QueryChain.of(this.mapper).select(SYS_USER_DATA_ROLE.ROLE_ID).where(SYS_USER_DATA_ROLE.USER_ID.eq(userId)).listAs(Long.class);
        SysUserRoleVO vo = new SysUserRoleVO();
        vo.setRoleInfoVOS(roleInfoVOS);
        vo.setSelectIds(roleIds);
        return vo;
    }

}