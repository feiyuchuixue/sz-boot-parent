package com.sz.admin.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.admin.system.pojo.po.SysDeptRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统部门-角色关联表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2025-07-15
 */
public interface SysDeptRoleMapper extends BaseMapper<SysDeptRole> {

    void insertBatchSysDeptRole(@Param("roleIds") List<Long> roleIds, @Param("deptId") Long deptId);

}
