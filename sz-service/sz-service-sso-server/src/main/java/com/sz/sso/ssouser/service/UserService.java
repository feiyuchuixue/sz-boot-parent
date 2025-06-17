package com.sz.sso.ssouser.service;

import com.mybatisflex.core.service.IService;
import com.sz.sso.ssouser.pojo.po.User;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.sso.ssouser.pojo.dto.UserCreateDTO;
import com.sz.sso.ssouser.pojo.dto.UserUpdateDTO;
import com.sz.sso.ssouser.pojo.dto.UserListDTO;
import com.sz.sso.ssouser.pojo.vo.UserVO;

/**
 * <p>
 * 平台用户表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-16
 */
public interface UserService extends IService<User> {

    void create(UserCreateDTO dto);

    void update(UserUpdateDTO dto);

    PageResult<UserVO> page(UserListDTO dto);

    List<UserVO> list(UserListDTO dto);

    void remove(SelectIdsDTO dto);

    UserVO detail(Object id);
}