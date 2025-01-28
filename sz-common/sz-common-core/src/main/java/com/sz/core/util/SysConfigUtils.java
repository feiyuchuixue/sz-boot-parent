package com.sz.core.util;

import com.sz.core.common.service.ConfService;

/**
 * 工具类，用于获取系统配置信息。
 *
 * @since 2024-01-10
 * @version 1.0
 */
public class SysConfigUtils {

    private SysConfigUtils() {
        throw new IllegalStateException("SysConfigUtils class Illegal");
    }

    /**
     * 根据指定的键获取配置信息的值。
     *
     * @param key
     *            配置项的键
     * @return 配置项的值
     */
    public static String getConfValue(String key) {
        ConfService confService = SpringApplicationContextUtils.getInstance().getBean(ConfService.class);
        return confService.getConfValue(key);
    }

    /**
     * 检查指定的配置项是否存在。
     *
     * @param key
     *            配置项的键
     * @return 如果配置项存在，则返回 true；否则返回 false
     */
    public static boolean hasConf(String key) {
        ConfService confService = SpringApplicationContextUtils.getInstance().getBean(ConfService.class);
        return confService.hasConfKey(key);
    }

}
