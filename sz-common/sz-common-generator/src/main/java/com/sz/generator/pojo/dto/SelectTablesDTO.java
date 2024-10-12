package com.sz.generator.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SelectTablesDTO
 * @Author sz
 * @Date 2023/12/4 10:36
 * @Version 1.0
 */
@Data
@Schema(description = "批量选中")
public class SelectTablesDTO {

    @Schema(description = "选中的tableName数组")
    private List<String> tableNames;

}
