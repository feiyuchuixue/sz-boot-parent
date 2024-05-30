package com.sz.admin.system.pojo.vo.sysclient;

import com.sz.security.pojo.ClientVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysClient查询返回
 * </p>
 *
 * @author sz
 * @since 2024-01-22
 */
@Data
@Schema(description = "SysClient返回vo")
public class SysClientVO extends ClientVO {

    @Schema(description = "是否锁定")
    private String isLock;

}