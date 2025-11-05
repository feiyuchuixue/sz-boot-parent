package com.sz.admin.system.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户类型")
public class SysUserTagDTO {

    @Schema(description = "用户ID")
    private List<Long> userIds;

    @Schema(description = "用户类型")
    private String userTagCd;

}
