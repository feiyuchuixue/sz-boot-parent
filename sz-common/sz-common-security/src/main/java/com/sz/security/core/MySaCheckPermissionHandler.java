package com.sz.security.core;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;
import com.sz.security.core.util.LoginUtils;

import java.lang.reflect.Method;

/**
 * @ClassName MySaCheckPermissionHandler
 * @Author sz
 * @Date 2024/10/9 14:16
 * @Version 1.0
 */
public class MySaCheckPermissionHandler implements SaAnnotationHandlerInterface<SaCheckPermission> {

    @Override
    public Class<SaCheckPermission> getHandlerAnnotationClass() {
        return SaCheckPermission.class;
    }

    @Override
    public void checkMethod(SaCheckPermission at, Method method) {
        _checkMethod(at.type(), at.value(), at.mode(), at.orRole());
    }

    public static void _checkMethod(String type, String[] value, SaMode mode, String[] orRole) {
        StpLogic stpLogic = SaManager.getStpLogic(type, false);

        String[] permissionArray = value;
        try {
            if (mode == SaMode.AND) {
                stpLogic.checkPermissionAnd(permissionArray);
            } else {
                stpLogic.checkPermissionOr(permissionArray);
            }
        } catch (NotPermissionException e) {
            // Start------------以下是自定义代码-------
            if (LoginUtils.isSuperAdmin())
                return;
            // End------------以上是自定义代码-------
            // 权限认证校验未通过，再开始角色认证校验
            for (String role : orRole) {
                String[] rArr = SaFoxUtil.convertStringToArray(role);
                // 某一项 role 认证通过，则可以提前退出了，代表通过
                if (stpLogic.hasRoleAnd(rArr)) {
                    return;
                }
            }
            throw e;
        }
    }
}
