package com.sz.admin.system.pojo.vo.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName UserOptionVO
 * @Author sz
 * @Date 2024/7/1 13:32
 * @Version 1.0
 */
@Data
@Schema(description = "select下拉用户项VO")
public class UserOptionVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

}
