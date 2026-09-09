package com.sz.logger.audit;

/**
 * 操作审计类型。
 */
public enum OperationType {
    /**
     * 根据 HTTP 方法自动推断。
     */
    AUTO,
    /**
     * 新增。
     */
    CREATE,
    /**
     * 修改。
     */
    UPDATE,
    /**
     * 删除。
     */
    DELETE,
    /**
     * 查询。
     */
    QUERY,
    /**
     * 导入。
     */
    IMPORT,
    /**
     * 导出。
     */
    EXPORT,
    /**
     * 登录。
     */
    LOGIN,
    /**
     * 登出。
     */
    LOGOUT,
    /**
     * 其他。
     */
    OTHER
}
