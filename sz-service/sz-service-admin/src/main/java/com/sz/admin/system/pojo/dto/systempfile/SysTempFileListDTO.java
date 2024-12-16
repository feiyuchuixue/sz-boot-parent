package com.sz.admin.system.pojo.dto.systempfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
/**
 * <p>
 * SysTempFile查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Schema(description = "SysTempFile查询DTO")
public class SysTempFileListDTO extends PageQuery {

    @Schema(description = "模版名")
    private String tempName;

}