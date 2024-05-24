package com.sz.platform.event;

import com.sz.admin.system.service.SysUserService;
import com.sz.core.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName EventListener 事件监听器
 * @Author sz
 * @Date 2024/2/29 16:27
 * @Version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalEventListener {

    private final SysUserService sysUserService;

    @EventListener
    public void handlePermissionChangeEvent(PermissionChangeEvent event) {
        PermissionMeta permissionMeta = event.getPayload();
        log.warn("[事件监听]-权限变更, data: {}", JsonUtils.toJsonString(permissionMeta));
        List<?> userIds = permissionMeta.getUserIds();
        for (Object userId : userIds) {
            sysUserService.syncUserInfo(userId);
        }
    }

}
