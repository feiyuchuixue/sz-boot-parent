package com.sz.admin.system.pojo.dto.sysdept;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * SysDept添加DTO
 * </p>
 *
 * @author sz
 * @since 2024-03-20
 */
@Data
@Schema(description = "SysDept查询DTO")
public class SysDeptListDTO extends PageQuery {

    @Schema(description =  "部门名称")
    private String name;


}