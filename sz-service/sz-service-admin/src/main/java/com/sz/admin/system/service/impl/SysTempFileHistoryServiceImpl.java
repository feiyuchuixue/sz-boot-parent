package com.sz.admin.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.system.service.SysTempFileHistoryService;
import com.sz.admin.system.pojo.po.SysTempFileHistory;
import com.sz.admin.system.mapper.SysTempFileHistoryMapper;

/**
 * <p>
 * 模版文件历史 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Service
@RequiredArgsConstructor
public class SysTempFileHistoryServiceImpl extends ServiceImpl<SysTempFileHistoryMapper, SysTempFileHistory> implements SysTempFileHistoryService {
}