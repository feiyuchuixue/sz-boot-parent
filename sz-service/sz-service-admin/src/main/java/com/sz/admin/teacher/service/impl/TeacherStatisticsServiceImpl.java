package com.sz.admin.teacher.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.teacher.service.TeacherStatisticsService;
import com.sz.admin.teacher.pojo.po.TeacherStatistics;
import com.sz.admin.teacher.mapper.TeacherStatisticsMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.Utils;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import java.io.Serializable;
import java.util.List;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsCreateDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsUpdateDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsListDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsImportDTO;
import org.springframework.web.multipart.MultipartFile;
import com.sz.excel.core.ExcelResult;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;

import com.sz.admin.teacher.pojo.vo.TeacherStatisticsVO;

/**
 * <p>
 * 教师统计总览表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2024-06-19
 */
@Service
@RequiredArgsConstructor
public class TeacherStatisticsServiceImpl extends ServiceImpl<TeacherStatisticsMapper, TeacherStatistics> implements TeacherStatisticsService {
    @Override
    public void create(TeacherStatisticsCreateDTO dto){
        TeacherStatistics teacherStatistics = BeanCopyUtils.copy(dto, TeacherStatistics.class);

        save(teacherStatistics);
    }

    @Override
    public void update(TeacherStatisticsUpdateDTO dto){
        TeacherStatistics teacherStatistics = BeanCopyUtils.copy(dto, TeacherStatistics.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(TeacherStatistics::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(teacherStatistics);
    }

    @Override
    public PageResult<TeacherStatisticsVO> page(TeacherStatisticsListDTO dto){
        Page<TeacherStatisticsVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), TeacherStatisticsVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<TeacherStatisticsVO> list(TeacherStatisticsListDTO dto){
        return listAs(buildQueryWrapper(dto), TeacherStatisticsVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public TeacherStatisticsVO detail(Object id){
        TeacherStatistics teacherStatistics = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(teacherStatistics);
        return BeanCopyUtils.copy(teacherStatistics, TeacherStatisticsVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(MultipartFile file) {
        ExcelResult<TeacherStatisticsImportDTO> excelResult = ExcelUtils.importExcel(file.getInputStream(), TeacherStatisticsImportDTO.class, true);
        List<TeacherStatisticsImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
    }

    @SneakyThrows
    @Override
    public void exportExcel(TeacherStatisticsListDTO dto, HttpServletResponse response) {
        List<TeacherStatisticsVO> list = list(dto);
        ServletOutputStream os = response.getOutputStream();
        ExcelUtils.exportExcel(list, "教师统计总览表", TeacherStatisticsVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(TeacherStatisticsListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(TeacherStatistics.class);
        wrapper.eq(TeacherStatistics::getYear, dto.getYear());
        wrapper.eq(TeacherStatistics::getMonth, dto.getMonth());
        wrapper.eq(TeacherStatistics::getDuringTime, dto.getDuringTime());
        wrapper.eq(TeacherStatistics::getTeacherId, dto.getTeacherId());
        wrapper.eq(TeacherStatistics::getTeacherCommonType, dto.getTeacherCommonType());
        wrapper.eq(TeacherStatistics::getTotalTeaching, dto.getTotalTeaching());
        wrapper.eq(TeacherStatistics::getTotalClassCount, dto.getTotalClassCount());
        wrapper.eq(TeacherStatistics::getTotalHours, dto.getTotalHours());
        wrapper.eq(TeacherStatistics::getCheckStatus, dto.getCheckStatus());
        wrapper.eq(TeacherStatistics::getCheckTime, dto.getCheckTime());
        wrapper.eq(TeacherStatistics::getLastSyncTime, dto.getLastSyncTime());
        return wrapper;
    }
}