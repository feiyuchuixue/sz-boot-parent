package com.sz.platform.event;

import com.sz.core.common.event.BaseEvent;

/**
 * @ClassName 权限变更事件（用户角色变更、角色权限变更）
 * @Author sz
 * @Date 2024/2/29 15:44
 * @Version 1.0
 */
public class PermissionChangeEvent extends BaseEvent<PermissionMeta> {

    public PermissionChangeEvent(Object source, PermissionMeta payload) {
        super(source, payload);
    }
}
