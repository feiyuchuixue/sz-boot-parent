package com.sz.platform;

public interface CenterUserConvertService {

    /**
     * 将 SSO 用户转换为平台用户
     * @param userId  SSO client 用户 ID
     * @return 平台用户 ID
     */
    Object convertUserIdToCenterId(Object userId);

    /**
     * 将平台用户转换为 SSO 用户
     *
     * @param centerId 平台用户 ID
     * @return SSO client 用户 ID
     */
    Object convertCenterIdToUserId(Object centerId);

}