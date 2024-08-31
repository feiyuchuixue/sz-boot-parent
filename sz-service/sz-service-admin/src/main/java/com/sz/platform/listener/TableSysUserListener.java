package com.sz.platform.listener;

import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.mysql.EntityChangeListener;
import com.sz.redis.RedisCache;

import static com.sz.platform.enums.DynamicDictEnum.DYNAMIC_USER_OPTIONS;

/**
 * sys_user表监听器
 *
 * @ClassName TableUserChangeListener
 * @Author sz
 * @Date 2024/8/22 10:00
 * @Version 1.0
 */
public class TableSysUserListener extends EntityChangeListener {

    @Override
    public void onInsert(Object o) {
        super.onInsert(o);
        onChange(o);
    }

    @Override
    public void onUpdate(Object o) {
        super.onUpdate(o);
        onChange(o);
    }

    /**
     * 触发change事件
     *
     * @param o
     */
    private void onChange(Object o) {
        RedisCache cache = SpringApplicationContextUtils.getBean(RedisCache.class);
        cache.clearDict(DYNAMIC_USER_OPTIONS.getTypeCode());
    }
}
