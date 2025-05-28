package com.sz.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sz
 * @since 2024/4/26 9:23
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "sz.wechat")
public class WechatProperties {

    @Schema(description = "小程序开发者配置")
    private MiniProgramProperties mini;

    @Schema(description = "企业微信开发者配置")
    private WorkProgramProperties work;

    @Data
    public static class MiniProgramProperties {

        @Schema(description = "小程序应用ID")
        private String appId;

        @Schema(description = "小程序应用密钥")
        private String appSecret;
    }

    @Data
    public static class WorkProgramProperties {

        @Schema(description = "企业ID; 登录企业微信管理后台,位于【我的企业-应用信息】-> 企业ID处获取; 文档：https://developer.work.weixin.qq.com/document/path/90665#corpid")
        private String corpId;

        @Schema(description = "企业微信应用密钥; 登录企业微信管理后台，位于【应用管理-应用-自建】-> 创建应用; 文档：https://developer.work.weixin.qq.com/document/path/90665#secret")
        private String corpSecret;

        @Schema(description = "企业微信应用凭证; 同上述corpSecret，位于“应用”secret属性的下方; 文档：https://developer.work.weixin.qq.com/document/path/90665#agentid")
        private Integer agentId;

    }
}
