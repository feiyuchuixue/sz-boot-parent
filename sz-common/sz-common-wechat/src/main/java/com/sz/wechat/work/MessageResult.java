package com.sz.wechat.work;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sz.wechat.pojo.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * MessageResult - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/4/23
 */
@Data
@Schema(description = "消息发送结果")
public class MessageResult extends ErrorMessage {

    @Schema(description = "不合法的userid，不区分大小写，统一转为小写")
    private String invaliduser;

    @Schema(description = "不合法的partyid")
    private String invalidparty;

    @Schema(description = "不合法的标签id")
    private String invalidtag;

    @Schema(description = "没有基础接口许可(包含已过期)的userid")
    private String unlicenseduser;

    @Schema(description = "消息id，用于撤回应用消息")
    private String msgid;

    @Schema(description = "仅消息类型为“按钮交互型”，“投票选择型”和“多项选择型”的模板卡片消息返回，应用可使用response_code调用更新模版卡片消息接口，72小时内有效，且只能使用一次")
    @JsonProperty("response_code")
    private String responseCode;

}
