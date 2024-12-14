package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysTempFileHistoryMapper;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryCreateDTO;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryListDTO;
import com.sz.admin.system.pojo.po.SysTempFileHistory;
import com.sz.admin.system.service.SysTempFileHistoryService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sz.admin.system.pojo.po.table.SysTempFileHistoryTableDef.SYS_TEMP_FILE_HISTORY;

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

    @Override
    public void create(SysTempFileHistoryCreateDTO dto) {
        SysTempFileHistory history = BeanCopyUtils.copy(dto, SysTempFileHistory.class);
        save(history);
    }

    @Override
    public PageResult<SysTempFileHistory> historyList(SysTempFileHistoryListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_TEMP_FILE_HISTORY.SYS_TEMP_FILE_ID.eq(dto.getSysTempFileId()));
        Page<SysTempFileHistory> page = page(PageUtils.getPage(dto), wrapper);
        return PageUtils.getPageResult(page);
    }

}