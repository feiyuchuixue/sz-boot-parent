package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysUserRole;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统用户-角色关联表 服务类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    List<String> getUserRolesByUserId(Long userId);

    /**
     * 批量查询多个用户的角色（IN 查询，减少 N 次单查为 1 次）
     *
     * @param userIds
     *            用户ID列表
     * @return userId -> 角色ID列表 的映射
     */
    Map<Long, List<String>> getUserRolesByUserIds(List<Long> userIds);
}
