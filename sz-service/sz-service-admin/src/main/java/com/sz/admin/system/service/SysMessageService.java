package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysmessage.Message;
import com.sz.admin.system.pojo.dto.sysmessage.SysMessageListDTO;
import com.sz.admin.system.pojo.po.SysMessage;
import com.sz.admin.system.pojo.vo.sysmessage.MessageCountVO;
import com.sz.admin.system.pojo.vo.sysmessage.SysMessageVO;
import com.sz.core.common.entity.PageResult;
import java.util.List;

/**
 * <p>
 * 消息管理 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
public interface SysMessageService extends IService<SysMessage> {

    void create(Message dto);

    PageResult<SysMessageVO> page(SysMessageListDTO dto);

    List<SysMessageVO> list(SysMessageListDTO dto);

    SysMessageVO detail(Object id);

    MessageCountVO countMyUnreadMessages();
}
