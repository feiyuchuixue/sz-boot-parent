package com.sz.core.util;

import com.sz.core.common.service.ConfService;

/**
 * 获取sysConf信息方法
 * 
 * @ClassName SysConfigUtils
 * @Author sz
 * @Date 2024/1/10 8:18
 * @Version 1.0
 */
public class SysConfigUtils {

    /**
     * 根据key获取config value
     * 
     * @param key
     * @return
     */
    public static String getConfValue(String key) {
        ConfService confService = SpringApplicationContextUtils.getBean(ConfService.class);
        return confService.getConfValue(key);
    }

    /**
     * 判断某个conf 是否存在
     * 
     * @param key
     * @return
     */
    public static boolean hasConf(String key) {
        ConfService confService = SpringApplicationContextUtils.getBean(ConfService.class);
        return confService.hasConfKey(key);
    }

}
