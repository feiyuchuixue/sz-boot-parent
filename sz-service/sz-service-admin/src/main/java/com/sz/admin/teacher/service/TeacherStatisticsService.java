package com.sz.admin.teacher.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsCreateDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsListDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsUpdateDTO;
import com.sz.admin.teacher.pojo.po.TeacherStatistics;
import com.sz.admin.teacher.pojo.vo.TeacherStatisticsVO;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 教师统计总览表 Service
 * </p>
 *
 * @author sz
 * @since 2024-02-19
 */
public interface TeacherStatisticsService extends IService<TeacherStatistics> {

    void create(TeacherStatisticsCreateDTO dto);

    void update(TeacherStatisticsUpdateDTO dto);

    PageResult<TeacherStatisticsVO> page(TeacherStatisticsListDTO dto);

    List<TeacherStatisticsVO> list(TeacherStatisticsListDTO dto);

    void remove(SelectIdsDTO dto);

    TeacherStatisticsVO detail(Long id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(TeacherStatisticsListDTO dto, HttpServletResponse response);

    List<TeacherStatisticsVO.TeacherTypeEnum> remoteSearch(String keyword);
}