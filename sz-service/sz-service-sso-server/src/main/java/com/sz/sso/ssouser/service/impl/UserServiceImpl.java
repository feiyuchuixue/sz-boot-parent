package com.sz.sso.ssouser.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.sso.ssouser.service.UserService;
import com.sz.sso.ssouser.pojo.po.User;
import com.sz.sso.ssouser.mapper.UserMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryChain;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.Utils;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import java.io.Serializable;
import java.util.List;
import com.sz.sso.ssouser.pojo.dto.UserCreateDTO;
import com.sz.sso.ssouser.pojo.dto.UserUpdateDTO;
import com.sz.sso.ssouser.pojo.dto.UserListDTO;
import com.sz.sso.ssouser.pojo.vo.UserVO;

/**
 * <p>
 * 平台用户表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-16
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public void create(UserCreateDTO dto){
        User ssoUser = BeanCopyUtils.copy(dto, User.class);
        save(ssoUser);
    }

    @Override
    public void update(UserUpdateDTO dto){
        User ssoUser = BeanCopyUtils.copy(dto, User.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(User::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(ssoUser);
    }

    @Override
    public PageResult<UserVO> page(UserListDTO dto){
        Page<UserVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), UserVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<UserVO> list(UserListDTO dto){
        return listAs(buildQueryWrapper(dto), UserVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public UserVO detail(Object id){
        User ssoUser = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(ssoUser);
        return BeanCopyUtils.copy(ssoUser, UserVO.class);
    }

    private static QueryWrapper buildQueryWrapper(UserListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(User.class);
        if (Utils.isNotNull(dto.getUsername())) {
            wrapper.like(User::getUsername, dto.getUsername());
        }
        if (Utils.isNotNull(dto.getNickname())) {
            wrapper.like(User::getNickname, dto.getNickname());
        }
        if (Utils.isNotNull(dto.getStatus())) {
            wrapper.eq(User::getStatus, dto.getStatus());
        }
        return wrapper;
    }
}