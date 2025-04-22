package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysMessageUser;
import java.util.List;

/**
 * <p>
 * 消息接收用户表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
public interface SysMessageUserService extends IService<SysMessageUser> {

    void batchInsert(Long messageId, List<Object> receiverIds);

    String getIsRead(Long messageId, Long receiverId);
}
