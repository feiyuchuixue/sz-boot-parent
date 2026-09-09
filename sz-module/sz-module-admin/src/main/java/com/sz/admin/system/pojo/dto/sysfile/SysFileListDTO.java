package com.sz.admin.system.pojo.dto.sysfile;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SysFileQueryDTO
 *
 * @author sz
 * @since 2023/8/31 0031
 */
@Data
@Schema(description = "公共文件搜索")
public class SysFileListDTO extends PageQuery {

    @Schema(description = "文件名")
    private String filename;
}
