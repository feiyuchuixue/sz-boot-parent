package com.sz.core.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictVO {

    {
        isDynamic = false;
    }

    @Schema(description = "字典id", example = "1000001")
    private String id;

    @Schema(description = "字典类型id", example = "1000")
    private Long sysDictTypeId;

    @Schema(description = "字典名称", example = "正常")
    private String codeName;

    @Schema(description = "别名", example = "normal")
    private String alias;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "回显样式", example = "success", allowableValues = "success,info,warning,danger")
    private String callbackShowStyle;

    @Schema(description = "是否锁定", example = "F", allowableValues = "T,F")
    private String isLock;

    @Schema(description = "是否展示", example = "T", allowableValues = "T,F")
    private String isShow;

    @Schema(description = "字典类型名", example = "账户状态")
    private String sysDictTypeName;

    @Schema(description = "字典类型码", example = "account_status")
    private String sysDictTypeCode;

    @Schema(description = "是否动态字典")
    @JsonProperty("isDynamic")
    private boolean isDynamic;
}
