package com.sz.ssoserver.ssouser.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mybatisflex.core.service.IService;
import com.sz.platform.pojo.SsoLoginParam;
import com.sz.ssoserver.ssouser.pojo.po.SsoUser;

/**
 * <p>
 * 平台用户表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
public interface SsoUserService extends IService<SsoUser> {

    SaTokenInfo ssoLogin(SsoLoginParam param);
}