package com.sz.core.common.service;

/**
 * 系统配置获取
 *
 * @author sz
 * @since 2024/1/9 15:25
 */
public interface ConfService {

    /**
     * 验证配置是否存在
     * 
     * @param key
     *            配置key
     * @return boolean
     */
    boolean hasConfKey(String key);

    /**
     * 根据配置key获取value
     * 
     * @param key
     *            配置key
     * @return 配置信息
     */
    String getConfValue(String key);

}
