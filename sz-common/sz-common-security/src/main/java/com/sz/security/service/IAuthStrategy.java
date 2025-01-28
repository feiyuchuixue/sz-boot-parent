package com.sz.security.service;

import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.security.pojo.ClientVO;
import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;

/**
 * 策略接口
 *
 * @author sz
 * @since 2024/1/23 9:38
 * @version 1.0
 */
public interface IAuthStrategy {

    String BASE_NAME = "AuthStrategy";

    static LoginVO login(LoginInfo info, ClientVO client, String grantType) {
        // 授权类型和客户端id
        String beanName = grantType + BASE_NAME;
        CommonResponseEnum.INVALID.message("无效的授权类型").assertFalse(SpringApplicationContextUtils.getInstance().containsBean(beanName));
        IAuthStrategy instance = SpringApplicationContextUtils.getInstance().getBean(beanName);
        return instance.login(info, client);
    }

    LoginVO login(LoginInfo info, ClientVO client);

}
