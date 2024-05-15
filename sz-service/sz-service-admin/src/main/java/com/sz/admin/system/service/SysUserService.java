package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysmenu.SysUserRoleDTO;
import com.sz.admin.system.pojo.dto.sysuser.*;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.admin.system.pojo.vo.sysuser.SysUserRoleVO;
import com.sz.admin.system.pojo.vo.sysuser.SysUserVO;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
public interface SysUserService extends IService<SysUser> {

    SysUserVO getSysUserByUsername(String username);

    SysUserVO getSysUserByUserId(Long userId);

    void register(RegisterUserDTO dto);

    /**
     * 创建用户
     *
     * @param dto 用户信息
     */
    void create(SysUserAddDTO dto);

    /**
     * 更新用户
     *
     * @param dto 用户信息
     */
    void update(SysUserAddDTO dto);

    /**
     * 删除用户
     *
     * @param dto 用户id数组
     */
    void remove(SelectIdsDTO dto);

    /**
     * 详情
     *
     * @param id id
     * @return {@link SysUser}
     */
    SysUser detail(Long id);

    PageResult<SysUserVO> list(SysUserListDTO dto);

    SysUserRoleVO findSysUserRole(Integer userId);

    void changeSysUserRole(SysUserRoleDTO dto);

    /**
     * 获取用户信息
     *
     * @return {@link SysUserVO}
     */
    SysUserVO getUserInfo();

    /**
     * 更改密码
     *
     * @param dto dto
     */
    void changePassword(SysUserPasswordDTO dto);

    /**
     * 重置密码
     *
     * @param id
     */
    void resetPassword(Long id);

    void syncUserInfo(Object userId);

    LoginUser buildLoginUser(String username, String password);

    LoginUser buildLoginUser(Long userId);

    void unlock(SelectIdsDTO dto);

    void bindUserDept(UserDeptDTO dto);
}
