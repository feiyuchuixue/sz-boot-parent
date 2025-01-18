package com.sz.security.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.sz.core.common.entity.LoginUser;
import com.sz.security.core.util.LoginUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sz
 * @since 2024/1/26 16:40
 * @version 1.0
 */
@Order(Integer.MIN_VALUE)
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = LoginUtils.getLoginUser();
        assert loginUser != null;
        return new ArrayList<>(loginUser.getPermissions());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = LoginUtils.getLoginUser();
        assert loginUser != null;
        return new ArrayList<>(loginUser.getRoles());
    }
}
