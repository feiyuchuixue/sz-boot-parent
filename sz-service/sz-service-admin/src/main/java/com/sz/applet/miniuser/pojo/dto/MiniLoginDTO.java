package com.sz.applet.miniuser.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName MiniLoginDTO
 * @Author sz
 * @Date 2024/4/26 14:25
 * @Version 1.0
 */
@Data
public class MiniLoginDTO {

    @Schema(description = "小程序登录凭证")
    private String code;
}
