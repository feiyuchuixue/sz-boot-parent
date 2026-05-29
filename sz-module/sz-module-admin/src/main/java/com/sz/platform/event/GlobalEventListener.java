package com.sz.platform.event;

import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * EventListener 事件监听器
 * 
 * @author sz
 * @since 2024/2/29 16:27
 * @version 1.0
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
        List<?> rawUserIds = permissionMeta.getUserIds();
        if (rawUserIds == null || rawUserIds.isEmpty()) {
            return;
        }
        // 将 userIds 转为 List<Long>，统一批量构建 LoginUser，减少 DB 查询
        List<Long> userIds = rawUserIds.stream()
                .map(id -> Long.parseLong(id.toString()))
                .distinct()
                .toList();
        Map<Long, LoginUser> loginUserMap = sysUserService.buildLoginUserBatch(userIds);
        // 逐个同步 SaSession 并发送 WebSocket 通知
        for (Long userId : userIds) {
            LoginUser loginUser = loginUserMap.get(userId);
            if (loginUser != null) {
                sysUserService.syncUserInfoWithLoginUser(userId, loginUser);
            } else {
                log.warn("[事件监听]-权限变更, userId:{} 未查到用户信息，跳过同步", userId);
            }
        }
    }

}
