package com.sz.admin.system.pojo.dto.scriptexport;

import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Script export request.
 *
 * @author sz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "脚本导出请求")
public class ScriptExportDTO extends SelectIdsDTO {

    @Schema(description = "SQL 方言：mysql/postgresql，为空时使用当前数据源")
    private String sqlDialect;

}
