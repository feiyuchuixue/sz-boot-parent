package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysuser.UserDeptDTO;
import com.sz.admin.system.pojo.po.SysUserDept;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户-部门关系表 Service
 * </p>
 *
 * @author sz
 * @since 2024-04-02
 */
public interface SysUserDeptService extends IService<SysUserDept> {

    @Transactional
    void bind(UserDeptDTO dto);

    void unbind(List<Long> userIds);
}