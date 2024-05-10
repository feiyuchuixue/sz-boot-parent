package com.sz.admin.system.pojo.vo.sysuser;

import lombok.Data;

import java.util.Date;

@Deprecated
@Data
public class UserAuthToken {

    private String accessToken;

    private String refreshToken;

    /**
     * access 超时时间
     */
    private Date accessExpire;

    /**
     * refresh 超时时间
     */
    private Date refreshExpire;

}
