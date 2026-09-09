package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysTempFileHistoryMapper;
import com.sz.admin.system.mapper.SysTempFileMapper;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryCreateDTO;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryListDTO;
import com.sz.admin.system.pojo.po.SysTempFileHistory;
import com.sz.admin.system.pojo.po.SysTempFile;
import com.sz.resource.enums.ResourceAccessResponseEnum;
import com.sz.admin.system.service.SysTempFileHistoryService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.sz.admin.system.pojo.po.table.SysTempFileHistoryTableDef.SYS_TEMP_FILE_HISTORY;

/**
 * <p>
 * 模版文件历史 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-12-05
 */
@Service
@RequiredArgsConstructor
public class SysTempFileHistoryServiceImpl extends ServiceImpl<SysTempFileHistoryMapper, SysTempFileHistory> implements SysTempFileHistoryService {

    private final SysTempFileMapper sysTempFileMapper;

    @Override
    public void create(SysTempFileHistoryCreateDTO dto) {
        SysTempFileHistory history = BeanCopyUtils.copy(dto, SysTempFileHistory.class);
        ResourceAccessResponseEnum.RESOURCE_REFERENCE_INVALID.message("模板历史必须且只能关联一个有效资源")
                .assertTrue(dto.getUrl() == null || dto.getUrl().size() != 1 || dto.getUrl().getFirst().getResourceId() == null);
        history.setSysFileId(dto.getUrl().getFirst().getResourceId());
        save(history);
    }

    @Override
    public PageResult<SysTempFileHistory> historyList(SysTempFileHistoryListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_TEMP_FILE_HISTORY.SYS_TEMP_FILE_ID.eq(dto.getSysTempFileId()));
        Page<SysTempFileHistory> page = page(PageUtils.getPage(dto), wrapper);
        return PageUtils.getPageResult(page);
    }

    @Override
    public void validateResourceAccess(Long id, Long resourceId) {
        SysTempFileHistory history = getById(id);
        if (history == null) {
            throw ResourceAccessResponseEnum.RESOURCE_NOT_FOUND.newException();
        }
        SysTempFile parent = sysTempFileMapper.selectOneById(history.getSysTempFileId());
        if (parent == null || !"F".equals(parent.getDelFlag())) {
            throw ResourceAccessResponseEnum.RESOURCE_NOT_FOUND.newException();
        }
        boolean matched = Objects.equals(history.getSysFileId(), resourceId) && history.getUrl() != null
                && history.getUrl().stream()
                .anyMatch(reference -> reference != null && Objects.equals(reference.getResourceId(), resourceId));
        ResourceAccessResponseEnum.ACCESS_DENIED.assertFalse(matched);
    }

}
