package com.sz.admin.system.pojo.dto.sysdict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "字典类型批量操作")
public class SysDictTypeSelectDTO {

    @Schema(description = "字典类型id数组")
    private List<String> ids;

}
