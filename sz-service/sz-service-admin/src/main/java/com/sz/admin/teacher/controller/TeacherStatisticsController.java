package com.sz.admin.teacher.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsCreateDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsListDTO;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsUpdateDTO;
import com.sz.admin.teacher.pojo.vo.TeacherStatisticsVO;
import com.sz.admin.teacher.service.TeacherStatisticsService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 教师统计总览表 Controller
 * </p>
 *
 * @author sz
 * @since 2024-02-19
 */
@Tag(name = "教师统计总览表")
@RestController
@RequestMapping("teacher-statistics")
@RequiredArgsConstructor
public class TeacherStatisticsController {

    private final TeacherStatisticsService teacherStatisticsService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "teacher.statistics.create", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@RequestBody TeacherStatisticsCreateDTO dto) {
        teacherStatisticsService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "teacher.statistics.update", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@RequestBody TeacherStatisticsUpdateDTO dto) {
        teacherStatisticsService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "teacher.statistics.remove", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        teacherStatisticsService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "teacher.statistics.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<TeacherStatisticsVO>> list(TeacherStatisticsListDTO dto) {
        return ApiPageResult.success(teacherStatisticsService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "teacher.statistics.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<TeacherStatisticsVO> detail(@PathVariable Long id) {
        return ApiResult.success(teacherStatisticsService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({@Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),})
    @SaCheckPermission(value = "teacher.statistics.import", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        teacherStatisticsService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "teacher.statistics.export", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/export")
    public void exportExcel(@RequestBody TeacherStatisticsListDTO dto, HttpServletResponse response) {
        teacherStatisticsService.exportExcel(dto, response);
    }

    @Operation(summary = "远程搜索测试接口")
    @GetMapping("/remote/{keyword}")
    public ApiResult<List<TeacherStatisticsVO.TeacherTypeEnum>> remoteSearch(@PathVariable String keyword) {
        return ApiResult.success(teacherStatisticsService.remoteSearch(keyword));
    }

}
