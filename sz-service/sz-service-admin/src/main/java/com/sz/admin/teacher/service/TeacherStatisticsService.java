package com.sz.admin.teacher.service;

import com.mybatisflex.core.service.IService;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;

import com.sz.admin.teacher.pojo.po.TeacherStatistics;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsCreateDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsUpdateDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsListDTO;
import com.sz.admin.teacher.pojo.vo.TeacherStatisticsVO;
    import com.sz.admin.teacher.pojo.dto.TeacherStatisticsImportDTO;
    import org.springframework.web.multipart.MultipartFile;
    import jakarta.servlet.ServletOutputStream;
    import jakarta.servlet.http.HttpServletResponse;


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

    void importExcel(MultipartFile file);

    void exportExcel(TeacherStatisticsListDTO dto, HttpServletResponse response);
}