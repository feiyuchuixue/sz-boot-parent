package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysMessageUserMapper;
import com.sz.admin.system.pojo.po.SysMessageUser;
import com.sz.admin.system.service.SysMessageUserService;
import java.util.List;

import com.sz.core.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息接收用户表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
@Service
@RequiredArgsConstructor
public class SysMessageUserServiceImpl extends ServiceImpl<SysMessageUserMapper, SysMessageUser> implements SysMessageUserService {

    @Override
    public void batchInsert(Long messageId, List<Object> receiverIds) {
        if (receiverIds.isEmpty()) {
            return;
        }
        List<SysMessageUser> sysMessageUsers = receiverIds.stream().map(receiverId -> {
            SysMessageUser sysMessageUser = new SysMessageUser();
            sysMessageUser.setMessageId(messageId);
            sysMessageUser.setReceiverId(Utils.getLongVal(receiverId));
            return sysMessageUser;
        }).toList();
        saveBatch(sysMessageUsers);
    }

    @Override
    public String getIsRead(Long messageId, Long receiverId) {
        SysMessageUser sysMessageUser = getOne(QueryWrapper.create().eq(SysMessageUser::getMessageId, messageId).eq(SysMessageUser::getReceiverId, receiverId));
        if (sysMessageUser == null) {
            return "F";
        }
        return sysMessageUser.getIsRead();
    }

}
