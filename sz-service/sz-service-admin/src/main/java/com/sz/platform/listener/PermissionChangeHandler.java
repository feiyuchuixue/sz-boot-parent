package com.sz.platform.listener;

import com.alibaba.fastjson.JSON;
import com.sz.core.common.entity.UserPermissionChangeMessage;
import com.sz.redis.handler.UserPermissionChangeMsgHandler;
import org.springframework.stereotype.Component;

@Component
public class PermissionChangeHandler implements UserPermissionChangeMsgHandler {
    @Override
    public void handlerMsg(UserPermissionChangeMessage message) {
        System.out.println(" [signal] message  = " + JSON.toJSON(message));
        // todo 同步更新变更用户的权限信息
        //StpUtil.getTokenSessionByToken(token).set()

    /*    // 用户权限发生变更，更新内存（删除内存中缓存的数据，下次查询时触发重新加载）
        if (message.isToChangeAll()) {
            SecurityUtils.loginUserMap.clear();
        } else {
            String username = message.getUsername();
            if (Utils.isNotNull(username)) {
                SecurityUtils.loginUserMap.remove(username);
            }
        }*/
    }
}
