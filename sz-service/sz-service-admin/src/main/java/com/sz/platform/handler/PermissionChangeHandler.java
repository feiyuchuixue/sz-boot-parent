package com.sz.platform.handler;

import com.sz.core.common.entity.UserPermissionChangeMessage;
import com.sz.core.util.JsonUtils;
import com.sz.redis.handler.UserPermissionChangeMsgHandler;
import org.springframework.stereotype.Component;

@Component
public class PermissionChangeHandler implements UserPermissionChangeMsgHandler {

    @Override
    public void handlerMsg(UserPermissionChangeMessage message) {
        System.out.println(" [signal] message  = " + JsonUtils.toJsonString(message));
        // todo 同步更新变更用户的权限信息
    }
}
