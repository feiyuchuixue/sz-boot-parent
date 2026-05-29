package com.sz.platform.listener;

import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.mysql.EntityChangeListener;
import com.sz.redis.RedisCache;

import static com.sz.core.common.constant.GlobalConstant.DYNAMIC_DICT_PREFIX;
import static com.sz.platform.enums.DynamicDictEnum.DYNAMIC_ROLE_OPTIONS;

/**
 * sys_role表的监听器
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/29
 */
public class TableSysRoleListener extends EntityChangeListener {

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
     *            对象
     */
    private void onChange(Object o) {
        RedisCache cache = SpringApplicationContextUtils.getInstance().getBean(RedisCache.class);
        cache.clearDict(DYNAMIC_DICT_PREFIX + DYNAMIC_ROLE_OPTIONS.getTypeCode());
    }

}
