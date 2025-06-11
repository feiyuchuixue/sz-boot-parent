package com.sz.security.core.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author sz
 * @version 1.0
 * @since 2024/1/22 16:47
 */
@Component
@Slf4j
public class CustomUserListener implements SaTokenListener {

    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
        log.info("user doLogin, userId:{}, token:{}, loginParameter: {}", loginId, tokenValue, loginParameter.toString());
    }

    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        log.info("user doLogout, userId:{}, token:{}", loginId, tokenValue);
    }

    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        log.info("user doKickout, userId:{}, token:{}", loginId, tokenValue);
    }

    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        log.info("user doReplaced, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被封禁时触发
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
        log.info("user doDisable, userId:{}, service:{}", loginId, service);
    }

    /**
     * 每次被解封时触发
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
        log.info("user doUntieDisable, userId:{}, service:{}", loginId, service);
    }

    /**
     * 每次打开二级认证时触发
     */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
        log.info("user doOpenSafe, token:{}, service:{}", tokenValue, service);
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
        log.info("user doCloseSafe, token:{}, service:{}", tokenValue, service);
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCreateSession(String id) {
        log.info("user doCreateSession, id:{}", id);
    }

    /**
     * 每次注销Session时触发
     */
    @Override
    public void doLogoutSession(String id) {
        log.info("user doLogoutSession, id:{}", id);
    }

    /**
     * 每次 Token 续期时触发
     */
    @Override
    public void doRenewTimeout(String loginType, Object loginId, String tokenValue, long timeout) {
        log.info("user doRenewTimeout, loginId:{}, token:{}, timeout:{}", loginId, tokenValue, timeout);
    }

}
