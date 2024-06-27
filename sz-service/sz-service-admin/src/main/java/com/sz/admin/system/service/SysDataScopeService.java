package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysDataScope;
import com.sz.admin.system.pojo.vo.sysdatascope.SysDataScopeVO;


/**
 * <p>
 * 自定义数据权限表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2024-06-27
 */
public interface SysDataScopeService extends IService<SysDataScope> {


    SysDataScopeVO findDeptDataScope(Integer deptId, String relationTypeCd);
}
