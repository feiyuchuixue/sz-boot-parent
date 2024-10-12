package com.sz.generator.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ImportTableDTO
 * @Author sz
 * @Date 2023/11/27 11:09
 * @Version 1.0
 */
@Data
@Schema(description = "导入表")
public class ImportTableDTO {

    @Schema(description = "表名")
    private List<String> tableName;

}
