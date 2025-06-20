package com.sz.ssoserver.ssouser.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.platform.enums.LoginTypeEnum;
import com.sz.platform.pojo.SsoLoginParam;
import com.sz.ssoserver.ssouser.mapper.SsoUserMapper;
import com.sz.ssoserver.ssouser.pojo.po.SsoUser;
import com.sz.ssoserver.ssouser.service.SsoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 平台用户表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
@Service
@RequiredArgsConstructor
public class SsoUserServiceImpl extends ServiceImpl<SsoUserMapper, SsoUser> implements SsoUserService {

    @Override
    public SaTokenInfo ssoLogin(SsoLoginParam param) {
        LoginTypeEnum loginType = param.getLoginType();
        if (loginType.equals(LoginTypeEnum.USERNAME)) {
            SsoUser ssoUser = checkPassword(param.getUsername(), param.getPassword());
            CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertNull(ssoUser);
            StpUtil.login(ssoUser.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            System.out.println("tokenInfo ==" + tokenInfo);
            return tokenInfo;
        }
        return null;
    }

    public SsoUser checkPassword(String username, String password) {
        SsoUser ssoUser = QueryChain.of(SsoUser.class).eq(SsoUser::getUsername, username).one();
        if (ssoUser == null) {
            return null;
        }
        return matchEncoderPwd(password, ssoUser.getPasswordHash()) ? ssoUser : null;
    }

    private boolean matchEncoderPwd(String pwd, String pwdEncoder) {
        return BCrypt.checkpw(pwd, pwdEncoder);
    }

}