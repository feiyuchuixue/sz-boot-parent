package com.sz.mysql;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;
import com.sz.core.common.entity.LoginUser;
import com.sz.security.core.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据表change事件处理
 * <p>
 * 约定：强制数据表类型 `create_id` int,`create_time` datetime,`update_id`
 * int,`update_time` datetime
 *
 * @ClassName MybatisInsertListener
 * @Author sz
 * @Date 2023/12/8 18:27
 * @Version 1.0
 */
@Slf4j
public class EntityChangeListener implements InsertListener, UpdateListener, SetListener {

    @Override
    public void onInsert(Object o) {
        setPropertyIfPresent(o, "createTime", LocalDateTime.now());
        if (StpUtil.isLogin()) {
            setPropertyIfPresent(o, "createId", StpUtil.getLoginIdAsLong());
            LoginUser loginUser = LoginUtils.getLoginUser();
            List<Long> deptOptions = loginUser.getDepts();
            if (deptOptions.isEmpty())
                return;
            setPropertyIfPresent(o, "deptScope", deptOptions);
        }

    }

    @Override
    public void onUpdate(Object o) {
        setPropertyIfPresent(o, "updateTime", LocalDateTime.now());
        if (!StpUtil.isLogin())
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
            // 获取对象的字段
            Field field = clazz.getDeclaredField(propertyName);
            // 设置字段为可访问，以便访问私有字段
            field.setAccessible(true);
            // 设置字段的值
            field.set(object, propertyValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 如果字段不存在，则忽略异常
            log.warn(" Fill EntityChangeField failed; Property {} not found. ", propertyName);
        }
    }

}
