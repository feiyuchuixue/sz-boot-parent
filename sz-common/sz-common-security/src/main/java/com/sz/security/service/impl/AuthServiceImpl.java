package com.sz.security.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.sz.core.common.entity.SocketMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.redis.WebsocketRedisService;
import com.sz.security.pojo.ClientVO;
import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;
import com.sz.security.service.AuthService;
import com.sz.security.service.ClientService;
import com.sz.security.service.IAuthStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 *
 * @author sz
 * @since 2022-10-01
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final WebsocketRedisService websocketRedisService;

    @Override
    public LoginVO loginClient(LoginInfo info) {
        String clientId = info.getClientId();
        ClientService clientService = SpringApplicationContextUtils.getInstance().getBean(ClientService.class);
        ClientVO client = clientService.getClientByClientId(clientId);
        // 验证clientId有效性
        CommonResponseEnum.CLIENT_INVALID.assertNull(client);
        // 验证client status 有效性
        CommonResponseEnum.CLIENT_BLOCKED.assertTrue(!("1003001").equals(client.getClientStatusCd()));
        return IAuthStrategy.login(info, client, info.getGrantType());
    }

    /**
     * 强制注销指定用户
     * 
     * @param id
     *            用户id
     */
    @Override
    public void kickOut(Long id) {
        TransferMessage tm = new TransferMessage();
        tm.setToUsers(Collections.singletonList(id + ""));
        SocketMessage sb = new SocketMessage();
        sb.setChannel(SocketChannelEnum.KICK_OFF);
        tm.setMessage(sb);
        websocketRedisService.sendServiceToWs(tm);
        StpUtil.logout(id);
    }

}
