package com.sz.admin.teacher.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.teacher.mapper.TeacherStatisticsMapper;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsCreateDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsListDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsUpdateDTO;
import com.sz.admin.teacher.pojo.po.TeacherStatistics;
import com.sz.admin.teacher.pojo.vo.TeacherStatisticsVO;
import com.sz.admin.teacher.service.TeacherStatisticsService;
import com.sz.admin.teacher.service.support.TeacherStatisticsExcelImporter;
import com.sz.resource.enums.ResourceAccessResponseEnum;
import com.sz.admin.system.service.SysResourceService;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.datascope.DataScopeSession;
import com.sz.core.util.*;
import com.sz.excel.imports.model.ExcelImportResultVO;
import com.sz.excel.utils.ExcelUtils;
import com.sz.resource.model.ResourceRef;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 教师统计总览表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-02-19
 */
@Service
@RequiredArgsConstructor
public class TeacherStatisticsServiceImpl extends ServiceImpl<TeacherStatisticsMapper, TeacherStatistics> implements TeacherStatisticsService {

    private final TeacherStatisticsExcelImporter excelImporter;

    private final HtmlContentSanitizer htmlContentSanitizer;

    private final SysResourceService sysResourceService;

    @Override
    public void create(TeacherStatisticsCreateDTO dto) {
        TeacherStatistics teacherStatistics = BeanCopyUtils.copy(dto, TeacherStatistics.class);
        teacherStatistics.setUrl(sysResourceService.normalizeForCreate(dto.getUrl(), "teacher.attachment", currentUserId(dto.getUrl())));
        teacherStatistics.setContentHtml(htmlContentSanitizer.sanitize(teacherStatistics.getContentHtml()));
        // 唯一性校验
        save(teacherStatistics);
    }

    @Override
    public void update(TeacherStatisticsUpdateDTO dto) {
        TeacherStatistics existing = getById(dto.getId());
        CommonResponseEnum.INVALID_ID.assertNull(existing);
        TeacherStatistics teacherStatistics = BeanCopyUtils.copy(dto, TeacherStatistics.class);
        teacherStatistics.setUrl(sysResourceService.normalizeForUpdate(existing.getUrl(), dto.getUrl(), "teacher.attachment", currentUserId(dto.getUrl())));
        teacherStatistics.setContentHtml(htmlContentSanitizer.sanitize(teacherStatistics.getContentHtml()));
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(TeacherStatistics::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
        // 唯一性校验
        saveOrUpdate(teacherStatistics);
    }

    @Override
    public PageResult<TeacherStatisticsVO> page(TeacherStatisticsListDTO dto) {
        try (var ignored = new DataScopeSession(TeacherStatistics.class)) {
            Page<TeacherStatisticsVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), TeacherStatisticsVO.class); // 调试sql
            return PageUtils.getPageResult(page);
        }
    }

    @Override
    public List<TeacherStatisticsVO> list(TeacherStatisticsListDTO dto) {
        try (var ignored = new DataScopeSession(TeacherStatistics.class)) {
            List<TeacherStatisticsVO> list = listAs(buildQueryWrapper(dto), TeacherStatisticsVO.class);
            return list;
        }
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public TeacherStatisticsVO detail(Long id) {
        TeacherStatistics teacherStatistics = findAccessibleById(id).orElse(null);
        CommonResponseEnum.INVALID_ID.assertNull(teacherStatistics);
        return BeanCopyUtils.copy(teacherStatistics, TeacherStatisticsVO.class);
    }

    @Override
    public Optional<TeacherStatistics> findAccessibleById(Long id) {
        try (var ignored = new DataScopeSession(TeacherStatistics.class)) {
            QueryWrapper wrapper = QueryWrapper.create().from(TeacherStatistics.class).eq(TeacherStatistics::getId, id);
            return Optional.ofNullable(getOne(wrapper));
        }
    }

    @Override
    public void validateResourceAccess(Long id, Long resourceId) {
        TeacherStatistics record = findAccessibleById(id)
                .orElseThrow(() -> ResourceAccessResponseEnum.RESOURCE_NOT_FOUND.newException());
        boolean matched = record.getUrl() != null && record.getUrl().stream()
                .anyMatch(reference -> reference != null && Objects.equals(reference.getResourceId(), resourceId));
        ResourceAccessResponseEnum.ACCESS_DENIED.assertFalse(matched);
    }

    @SneakyThrows
    @Override
    public ExcelImportResultVO importExcel(ImportExcelDTO dto) {
        return excelImporter.importExcel(dto);
    }

    @SneakyThrows
    @Override
    public void exportExcel(TeacherStatisticsListDTO dto, HttpServletResponse response) {
        List<TeacherStatisticsVO> list = list(dto);
        String fileName = "教师统计";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, fileName, TeacherStatisticsVO.class, os, true);
    }

    private static QueryWrapper buildQueryWrapper(TeacherStatisticsListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(TeacherStatistics.class);
        if (Utils.isNotNull(dto.getYear())) {
            wrapper.eq(TeacherStatistics::getYear, dto.getYear());
        }
        if (Utils.isNotNull(dto.getMonth())) {
            wrapper.eq(TeacherStatistics::getMonth, dto.getMonth());
        }
        if (Utils.isNotNull(dto.getDuringTime())) {
            wrapper.eq(TeacherStatistics::getDuringTime, dto.getDuringTime());
        }
        if (Utils.isNotNull(dto.getTeacherId())) {
            wrapper.eq(TeacherStatistics::getTeacherId, dto.getTeacherId());
        }
        if (Utils.isNotNull(dto.getTeacherCommonType())) {
            wrapper.eq(TeacherStatistics::getTeacherCommonType, dto.getTeacherCommonType());
        }
        if (Utils.isNotNull(dto.getTotalTeaching())) {
            wrapper.eq(TeacherStatistics::getTotalTeaching, dto.getTotalTeaching());
        }
        if (Utils.isNotNull(dto.getTotalClassCount())) {
            wrapper.eq(TeacherStatistics::getTotalClassCount, dto.getTotalClassCount());
        }
        if (Utils.isNotNull(dto.getTotalHours())) {
            wrapper.eq(TeacherStatistics::getTotalHours, dto.getTotalHours());
        }
        if (Utils.isNotNull(dto.getCheckStatus())) {
            wrapper.eq(TeacherStatistics::getCheckStatus, dto.getCheckStatus());
        }
        if (Utils.isNotNull(dto.getCheckTimeStart()) && Utils.isNotNull(dto.getCheckTimeEnd())) {
            wrapper.between(TeacherStatistics::getCheckTime, dto.getCheckTimeStart(), dto.getCheckTimeEnd());
        }
        if (Utils.isNotNull(dto.getRemoteTeacherId())) {
            wrapper.eq(TeacherStatistics::getId, dto.getRemoteTeacherId());
        }
        return wrapper;
    }

    private Long currentUserId(List<ResourceRef> references) {
        return references == null || references.isEmpty() ? null : StpUtil.getLoginIdAsLong();
    }

    @SneakyThrows
    @Override
    public List<TeacherStatisticsVO.TeacherTypeEnum> remoteSearch(String keyword) {
        Thread.sleep(1000);
        QueryWrapper wrapper = QueryWrapper.create().from(TeacherStatistics.class).like(TeacherStatistics::getTeacherId, keyword);
        return listAs(wrapper, TeacherStatisticsVO.TeacherTypeEnum.class);
    }

}
