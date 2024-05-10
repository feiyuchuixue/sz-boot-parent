package com.sz.www.sysregion.pojo.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;

/**
 * <p>
 * SysRegion查询返回
 * </p>
 *
 * @author sz
 * @since 2024-04-25
 */
@Data
@Schema(description = "SysRegion返回vo")
public class SysRegionVO {

    @ExcelIgnore
    @Schema(description =  " 行政区id")
    private Integer id;

    @ExcelProperty(value = "行政区名称")
    @Schema(description =  "行政区名称")
    private String regionName;


}