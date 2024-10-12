package com.sz.redis.handler;

import com.sz.core.common.entity.UserPermissionChangeMessage;

public interface UserPermissionChangeMsgHandler {

    void handlerMsg(UserPermissionChangeMessage message);
}