package com.sz.admin.system.pojo.vo.sysdict;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String typeCode;

    private String typeName;

    @JsonProperty("isDynamic")
    private boolean isDynamic = false;

}
