package com.sz.admin.system.pojo.dto.sysresource;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 资源列表查询 DTO
 *
 * @author sz-admin
 */
@Data
@Schema(description = "资源列表查询")
public class SysResourceListDTO extends PageQuery {

    @Schema(description = "原始文件名（模糊搜索）")
    private String originName;

    @Schema(description = "场景编码（精确筛选）")
    private String sceneCode;

}
