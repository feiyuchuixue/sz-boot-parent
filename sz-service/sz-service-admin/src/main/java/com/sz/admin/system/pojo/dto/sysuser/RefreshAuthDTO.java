package com.sz.admin.system.pojo.dto.sysuser;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Deprecated
@Data
@Schema(description = "refresh刷新")
public class RefreshAuthDTO {

    private String refreshToken;

}
