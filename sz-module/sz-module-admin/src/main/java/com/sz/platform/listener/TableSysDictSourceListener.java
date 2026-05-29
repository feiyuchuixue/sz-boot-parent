package com.sz.platform.listener;

import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.db.EntityChangeListener;
import com.sz.redis.RedisCache;

import static com.sz.core.common.constant.GlobalConstant.DYNAMIC_DICT_PREFIX;
import static com.sz.platform.enums.DynamicDictEnum.DYNAMIC_DICT_SOURCE_OPTIONS;

/**
 * sys_dict_source 表监听器 字典来源数据变更时清除动态字典缓存
 *
 * TableSysDictSourceListener
 *
 * @author sz-admin
 * @since 2026-05-21
 * @version 1.0
 */
public class TableSysDictSourceListener extends EntityChangeListener {

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
     * 触发 change 事件，清除字典来源动态字典缓存
     *
     * @param o
     *            对象
     */
    private void onChange(Object o) {
        RedisCache cache = SpringApplicationContextUtils.getInstance().getBean(RedisCache.class);
        cache.clearDict(DYNAMIC_DICT_PREFIX + DYNAMIC_DICT_SOURCE_OPTIONS.getTypeCode());
    }

}
