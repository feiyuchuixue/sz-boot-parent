package com.sz.security.service;

import com.sz.security.pojo.ClientVO;

/**
 * @author sz
 * @since 2024/2/18 8:42
 * @version 1.0
 */
public interface ClientService {

    ClientVO getClientByClientId(Object id);

}
