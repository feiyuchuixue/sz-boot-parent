package com.sz.admin.system.pojo.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "一次性认证返回信息")
public class ChallengeVO {

    @Schema(description = "请求id")
    private String requestId;

    @Schema(description = "加密key")
    private String secretKey;

}
