package com.sz.admin.system.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * SysLoginLog添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
@Data
@Accessors(chain = true)
@Schema(description = "SysLoginLog添加DTO")
public class SysLoginLogCreateDTO {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "登陆状态")
    private String loginStatus;

    @Schema(description = "登陆时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    @Schema(description = "登陆ip地址")
    private String ipAddress;

    @Schema(description = "登陆地点")
    private String loginLocation;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "提示消息")
    private String msg;

    @Schema(description = "备注")
    private String remark;

}