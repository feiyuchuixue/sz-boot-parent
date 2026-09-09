package com.sz.admin.system.pojo.vo.sysmessage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * SysMessageUser返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
@Data
@Schema(description = "SysMessageUser返回vo")
public class SysMessageUserVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "消息ID")
    private Long messageId;

    @Schema(description = "接收人ID")
    private Long receiverId;

    @Schema(description = "是否已读")
    private String isRead;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;
}
