package com.sz.logger.audit;

/**
 * 响应体记录策略。
 */
public enum BodyRecordMode {
    /**
     * 跟随全局配置。
     */
    DEFAULT,
    /**
     * 当前接口强制不记录响应体。
     */
    NEVER
}
