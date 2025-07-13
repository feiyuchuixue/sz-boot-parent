package com.sz.core.common.dict;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DictTypeVO
 * 
 * @author sz
 * @since 2024/8/22 11:17
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictTypeVO {

    @Schema(description = "字典类型code")
    private String typeCode;

    @Schema(description = "字典类型名称")
    private String typeName;

    @Schema(description = "是否是动态字典")
    @JsonProperty("isDynamic")
    private boolean isDynamic = false;

}
