package com.sz.security.core.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.LoginUser;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author sz
 * @since 2024/1/24 9:38
 * @version 1.0
 */
@Slf4j
public class LoginUtils {

    private LoginUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final String USER_KEY = "loginUser";

    public static void performLogin(LoginUser loginUser, SaLoginParameter parameter, Map<String, Object> extraData) {
        parameter = ObjectUtil.defaultIfNull(parameter, new SaLoginParameter());
        parameter.setExtraData(extraData);
        // 登录，生成token
        StpUtil.login(loginUser.getUserInfo().getId(), parameter);
        StpUtil.getTokenSession().set(USER_KEY, loginUser);
        // 使用全局配置而不使用model独立配置时间的问题
        StpUtil.getTokenSession().updateTimeout(parameter.getTimeout());
        StpUtil.getSession().updateTimeout(parameter.getTimeout());
    }


    public static void performLogin(LoginUser loginUser, SaLoginModel model, Map<String, Object> extraData) {
        model = ObjectUtil.defaultIfNull(model, new SaLoginModel());
        model.setExtraData(extraData);
        // 登录，生成token
        StpUtil.login(loginUser.getUserInfo().getId(), model);
        StpUtil.getTokenSession().set(USER_KEY, loginUser);
        // 使用全局配置而不使用model独立配置时间的问题
        StpUtil.getTokenSession().updateTimeout(model.getTimeout());
        StpUtil.getSession().updateTimeout(model.getTimeout());
    }

    public static void performMiniLogin(Object userId, Object loginUser, SaLoginModel model, Map<String, Object> extraData) {
        model = ObjectUtil.defaultIfNull(model, new SaLoginModel());
        model.setExtraData(extraData);
        // 登录，生成token
        StpUtil.login(userId, model);
        StpUtil.getTokenSession().set(USER_KEY, loginUser);
        // 使用全局配置而不使用model独立配置时间的问题
        StpUtil.getTokenSession().updateTimeout(model.getTimeout());
        StpUtil.getSession().updateTimeout(model.getTimeout());
    }

    /**
     * 获取用户
     */
    public static LoginUser getLoginUser() {
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (LoginUser) session.get(USER_KEY);
    }

    /**
     * 根据token获取用户信息
     *
     * @param token
     *            token
     * @return 用户信息
     */
    public static LoginUser getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (LoginUser) session.get(USER_KEY);
    }

    /**
     * 是否是超级管理员
     *
     * @return 是否是超级管理员
     */
    public static boolean isSuperAdmin() {
        if (!StpUtil.isLogin())
            return false;
        LoginUser loginUser = getLoginUser();
        if (loginUser == null)
            return false;
        return loginUser.getRoles().contains(GlobalConstant.SUPER_ROLE);
    }

}
