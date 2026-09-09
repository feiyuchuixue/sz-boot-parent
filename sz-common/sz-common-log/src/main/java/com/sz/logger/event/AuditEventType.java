package com.sz.logger.event;

/**
 * 审计事件类型。
 */
public enum AuditEventType {
    /**
     * 操作成功。
     */
    OPERATION_SUCCESS,
    /**
     * 操作失败或业务返回失败码。
     */
    OPERATION_FAIL,
    /**
     * 主审计记录保存失败。
     */
    AUDIT_SAVE_FAILED
}
