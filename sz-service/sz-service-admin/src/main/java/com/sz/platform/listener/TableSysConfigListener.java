package com.sz.platform.listener;

import com.sz.admin.system.pojo.po.SysConfig;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.mysql.EntityChangeListener;
import com.sz.redis.RedisCache;
import com.sz.socket.SocketService;

public class TableSysConfigListener extends EntityChangeListener {

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

    private void onChange(Object o) {
        SysConfig sysConfig = (SysConfig) o;
        if (("T").equals(sysConfig.getFrontendVisible())) {
            RedisCache cache = SpringApplicationContextUtils.getInstance().getBean(RedisCache.class);
            cache.putFrontendConfig(sysConfig.getConfigKey(), sysConfig.getConfigValue());
        }
        // socket 推送，参数更新事件
        SocketService service = SpringApplicationContextUtils.getInstance().getBean(SocketService.class);
        service.syncFrontendConfig();
    }

}