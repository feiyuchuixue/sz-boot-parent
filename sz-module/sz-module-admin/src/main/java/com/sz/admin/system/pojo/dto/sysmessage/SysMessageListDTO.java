package com.sz.admin.system.pojo.dto.sysmessage;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysMessage查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
@Data
@Schema(description = "SysMessage查询DTO")
public class SysMessageListDTO extends PageQuery {

    public SysMessageListDTO(String messageTypeCd) {
        this.messageTypeCd = messageTypeCd;
    }

    @Schema(description = "消息类型", allowableValues = {"", "msg", "todo"})
    private String messageTypeCd;

    @Schema(description = "已读未读", allowableValues = {"", "read", "unread"})
    private String readType;

}
