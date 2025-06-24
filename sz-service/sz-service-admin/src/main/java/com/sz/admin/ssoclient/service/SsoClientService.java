package com.sz.admin.ssoclient.service;

import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import com.sz.security.pojo.LoginVO;

public interface SsoClientService {
    LoginVO login(SaCheckTicketResult ctr);
}
