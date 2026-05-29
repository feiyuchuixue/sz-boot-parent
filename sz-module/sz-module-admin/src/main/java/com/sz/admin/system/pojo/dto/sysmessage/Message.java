package com.sz.admin.system.pojo.dto.sysmessage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "SysMessage添加DTO")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Schema(description = "消息类型")
    private String messageTypeCd;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "接收人id")
    private List<Object> receiverIds;
}
