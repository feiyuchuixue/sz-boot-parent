package com.sz.admin.system.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import com.sz.core.common.entity.PageQuery;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
/**
 * <p>
 * SysLoginLog查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
@Data
@Accessors(chain = true)
@Schema(description = "SysLoginLog查询DTO")
public class SysLoginLogListDTO extends PageQuery {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "登陆状态")
    private String loginStatus;

    @Schema(description = "登陆时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTimeStart;

    @Schema(description = "登陆时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTimeEnd;

}