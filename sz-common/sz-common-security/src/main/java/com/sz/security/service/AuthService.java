package com.sz.security.service;

import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;

/**
 * @ClassName AuthService
 * @Author sz
 * @Date 2024/1/22 17:24
 * @Version 1.0
 */
public interface AuthService {

    LoginVO loginClient(LoginInfo info);
}
