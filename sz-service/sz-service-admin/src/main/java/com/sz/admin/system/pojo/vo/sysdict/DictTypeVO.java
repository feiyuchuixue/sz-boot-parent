package com.sz.admin.system.pojo.vo.sysdict;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @ClassName DictTypeVO
 * @Author sz
 * @Date 2024/8/22 11:17
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictTypeVO {

    {
        isDynamic = false;
    }

    private String typeCode;

    private String typeName;

    @JsonProperty("isDynamic")
    private boolean isDynamic;

}
