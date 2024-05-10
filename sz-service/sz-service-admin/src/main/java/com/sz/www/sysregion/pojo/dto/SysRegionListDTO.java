package com.sz.www.sysregion.pojo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * SysRegion添加DTO
 * </p>
 *
 * @author sz
 * @since 2024-04-25
 */
@Data
@Schema(description = "SysRegion查询DTO")
public class SysRegionListDTO extends PageQuery {

    @Schema(description =  "行政区名称")
    private String regionName;


}