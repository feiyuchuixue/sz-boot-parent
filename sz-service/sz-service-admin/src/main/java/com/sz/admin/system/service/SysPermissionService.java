package com.sz.admin.system.service;

import java.util.Set;

/**
 * @ClassName SysPermissionService
 * @Author sz
 * @Date 2024/2/4 15:12
 * @Version 1.0
 */
public interface SysPermissionService {
    Set<String> getMenuPermissions(Long userId);

    Set<String> getRoles(Long userId);
}
