package com.sz.admin.system.pojo.dto.systempfile;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysTempFileHistory查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Schema(description = "SysTempFileHistory查询DTO")
public class SysTempFileHistoryListDTO extends PageQuery {

    @Schema(description = "模板文件ID")
    private Long sysTempFileId;

}