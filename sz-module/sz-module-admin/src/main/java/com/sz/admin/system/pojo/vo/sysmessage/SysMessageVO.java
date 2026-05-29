package com.sz.admin.system.pojo.vo.sysmessage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * SysMessage返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
@Data
@Schema(description = "SysMessage返回vo")
public class SysMessageVO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "消息类型")
    private String messageTypeCd;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "菜单路由地址")
    private String menuRouter;

    @Schema(description = "是否已读")
    private String isRead;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
