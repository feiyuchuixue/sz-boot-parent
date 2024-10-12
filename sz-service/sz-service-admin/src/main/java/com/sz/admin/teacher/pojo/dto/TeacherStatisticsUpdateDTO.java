package com.sz.admin.teacher.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * TeacherStatistics添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-06-19
 */
@Data
@Schema(description = "TeacherStatistics修改DTO")
public class TeacherStatisticsUpdateDTO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "统计年限")
    private String year;

    @Schema(description = "统计月份")
    private String month;

    @Schema(description = "统计年月")
    private String duringTime;

    @Schema(description = "教师id")
    private String teacherId;

    @Schema(description = "讲师区分类型")
    private Integer teacherCommonType;

    @Schema(description = "授课总数")
    private Integer totalTeaching;

    @Schema(description = "服务班次数")
    private Integer totalClassCount;

    @Schema(description = "课时总数")
    private BigDecimal totalHours;

    @Schema(description = "核对状态")
    private Integer checkStatus;

    @Schema(description = "核对时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;

    @Schema(description = "最近一次同步时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSyncTime;

    @Schema(description = "备注")
    private String remark;

}