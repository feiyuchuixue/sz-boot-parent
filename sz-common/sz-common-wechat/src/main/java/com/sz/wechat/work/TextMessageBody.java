package com.sz.wechat.work;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TextMessageBody - 简要描述该类的功能.
 * <p>
 * API详见
 * https://developer.work.weixin.qq.com/document/path/96458#%E6%96%87%E6%9C%AC%E6%B6%88%E6%81%AF
 * https://developer.work.weixin.qq.com/document/path/90236#%E6%96%87%E6%9C%AC%E6%B6%88%E6%81%AF
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/4/23
 */
@Data
@Schema(description = "发送文本消息实体类")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextMessageBody {

    @Schema(description = "指定接收消息的成员，成员ID列表（多个接收者用‘|’分隔，最多支持1000个）。\n" + "特殊情况：指定为\"@all\"，则向该企业应用的全部成员发送")
    private String touser;

    /*
     * @Schema(description = "指定接收消息的部门，部门ID列表，多个接收者用‘|’分隔，最多支持100个。\n" +
     * "当touser为\"@all\"时忽略本参数") private String toparty;
     * 
     * @Schema(description = "指定接收消息的标签，标签ID列表，多个接收者用‘|’分隔，最多支持100个。\n" +
     * "当touser为\"@all\"时忽略本参数") private String totag;
     */

    @Schema(description = "消息类型，此时固定为：text")
    private String msgtype = "text";

    @Schema(description = "企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值")
    private Integer agentid;

    private Text text;

    @Schema(description = "表示是否是保密消息，0表示可对外分享，1表示不能分享且内容显示水印，默认为0")
    private Integer safe = 0;

    /*
     * @Schema(description = "是否开启id转译,0表示否，1表示是，默认0。")
     * 
     * @JsonProperty("enable_id_trans") private Integer enableIdTrans = 0;
     */

    @Data
    @Schema(description = "文本消息体")
    public static class Text {

        public Text(String content) {
            this.content = content;
        }

        @Schema(description = "消息内容，最长不超过2048个字节，超过将截断（支持id转译）")
        private String content;
    }

}
