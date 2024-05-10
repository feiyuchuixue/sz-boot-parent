package com.sz.security.service;

import com.sz.security.pojo.ClientVO;

/**
 * @ClassName ClientService
 * @Author sz
 * @Date 2024/2/18 8:42
 * @Version 1.0
 */
public interface ClientService {

    ClientVO getClientByClientId(Object id);

}
