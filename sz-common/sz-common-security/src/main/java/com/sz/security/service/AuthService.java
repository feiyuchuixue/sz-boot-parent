package com.sz.security.service;

import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;

/**
 * @author sz
 * @since 2024/1/22 17:24
 * @version 1.0
 */
public interface AuthService {

    LoginVO loginClient(LoginInfo info);

    void kickOut(Long userId);
}
