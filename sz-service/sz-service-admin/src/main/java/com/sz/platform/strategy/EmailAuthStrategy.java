package com.sz.platform.strategy;

import com.sz.security.pojo.ClientVO;
import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;
import com.sz.security.service.IAuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName EmailAuthStrategy
 * @Author sz
 * @Date 2024/1/23 10:29
 * @Version 1.0
 */
@Slf4j
@Service("email" + IAuthStrategy.BASE_NAME)
public class EmailAuthStrategy implements IAuthStrategy {

    @Override
    public LoginVO login(LoginInfo body, ClientVO client) {
        return null;
    }
}
