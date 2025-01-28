package com.sz.mysql;

import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;
import com.sz.core.common.entity.LoginUser;
import com.sz.security.core.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据表变更事件处理器。
 * <p>
 * 约定：数据表必须包含以下字段：
 * <ul>
 * <li>`create_id` - int 类型，创建者 ID</li>
 * <li>`create_time` - datetime 类型，创建时间</li>
 * <li>`update_id` - int 类型，更新者 ID</li>
 * <li>`update_time` - datetime 类型，更新时间</li>
 * </ul>
 *
 * @since 2023-12-08
 * @version 1.0
 */
@Slf4j
public class EntityChangeListener implements InsertListener, UpdateListener, SetListener {

    @Override
    public void onInsert(Object o) {
        setPropertyIfPresent(o, "createTime", LocalDateTime.now());
        if (isNotLogin()) {
            return;
        }
        setPropertyIfPresent(o, "createId", StpUtil.getLoginIdAsLong());
        LoginUser loginUser = LoginUtils.getLoginUser();
        List<Long> deptOptions = loginUser.getDepts();
        if (deptOptions.isEmpty())
            return;
        setPropertyIfPresent(o, "deptScope", deptOptions);
    }

    @Override
    public void onUpdate(Object o) {
        setPropertyIfPresent(o, "updateTime", LocalDateTime.now());
        if (isNotLogin())
            return;
        setPropertyIfPresent(o, "updateId", StpUtil.getLoginIdAsLong());
    }

    @Override
    public Object onSet(Object entity, String property, Object value) {
        return value;
    }

    private void setPropertyIfPresent(Object object, String propertyName, Object propertyValue) {
        try {
            // 获取对象的 Class 对象
            Class<?> clazz = object.getClass();
            // 获取属性名称对应的 setter 方法名称
            String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            // 获取 setter 方法
            Method setterMethod = clazz.getMethod(setterName, propertyValue.getClass());
            // 调用 setter 方法设置字段值
            setterMethod.invoke(object, propertyValue);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // 如果字段不存在或者 setter 方法不可访问，则忽略异常
            log.warn(" Fill EntityChangeField failed; Property {} not found or inaccessible. ", propertyName);
        }
    }

    private boolean isNotLogin() {
        try {
            return !StpUtil.isLogin();
        } catch (NotWebContextException e) {
            // 处理非 Web 环境异常，返回未登录
            return true;
        } catch (Exception e) {
            // 记录所有其他异常，并返回未登录
            log.error("[EntityChangeListener] Unexpected error during login check: {}", e.getMessage(), e);
            return true;
        }
    }

}
