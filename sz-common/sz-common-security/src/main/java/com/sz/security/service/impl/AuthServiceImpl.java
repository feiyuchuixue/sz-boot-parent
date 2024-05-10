package com.sz.security.service.impl;

import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.security.pojo.ClientVO;
import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;
import com.sz.security.service.AuthService;
import com.sz.security.service.ClientService;
import com.sz.security.service.IAuthStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author sz
 * @since 2022-10-01
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginVO loginClient(LoginInfo info) {
        String clientId = info.getClientId();
        ClientService clientService = SpringApplicationContextUtils.getBean(ClientService.class);
        ClientVO client = clientService.getClientByClientId(clientId);
        // 验证clientId有效性
        CommonResponseEnum.CLIENT_INVALID.assertNull(client);
        // 验证client status 有效性
        CommonResponseEnum.CLIENT_BLOCKED.assertTrue(!("1003001").equals(client.getClientStatusCd()));
        LoginVO login = IAuthStrategy.login(info, client, info.getGrantType());
        return login;
    }

}
