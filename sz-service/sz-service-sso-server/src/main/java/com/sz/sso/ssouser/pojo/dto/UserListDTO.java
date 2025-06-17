package com.sz.sso.ssouser.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
/**
 * <p>
 * User查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-16
 */
@Data
@Schema(description = "User查询DTO")
public class UserListDTO extends PageQuery {

    @Schema(description =  "用户名，唯一")
    private String username;

    @Schema(description =  "昵称")
    private String nickname;

    @Schema(description =  "状态（0：正常，1：禁用，2：待验证）")
    private String status;

}