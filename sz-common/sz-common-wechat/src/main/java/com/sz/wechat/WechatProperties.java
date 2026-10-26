package com.sz.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序与企业微信接口凭据。属性绑定本身不校验完整性，对应服务方法被调用时才读取并发送相关字段。
 *
 * @author sz
 * @since 2024/4/26 9:23
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "sz.wechat")
public class WechatProperties {

    /**
     * 微信小程序接口凭据。未使用 MiniWechatService 时可不配置；调用获取 access_token 或小程序登录时必须提供本对象及其中两项
     * 凭据，当前代码不做启动期完整性校验。
     */
    @Schema(description = "微信小程序接口凭据；调用小程序 access_token 或登录能力时必须配置。")
    private MiniProgramProperties mini;

    /**
     * 企业微信自建应用配置。未使用 WorkWechatService 时可不配置；获取 access_token 需要企业 ID 和应用
     * Secret，发送应用消息 还需要应用 AgentId，当前代码不做启动期完整性校验。
     */
    @Schema(description = "企业微信自建应用配置；获取 access_token 和发送应用消息时按能力提供对应字段。")
    private WorkProgramProperties work;

    @Data
    public static class MiniProgramProperties {

        /**
         * 微信小程序 AppId。与 app-secret 一起用于获取小程序 access_token 和调用小程序登录接口；当前代码直接把本值放入微信 请求
         * URI，不做非空校验。
         *
         * @schemaExample ${WECHAT_MINI_APP_ID} | 从环境变量读取小程序 AppId
         */
        @Schema(description = "微信小程序 AppId，用于获取小程序 access_token 和调用小程序登录接口。")
        private String appId;

        /**
         * 微信小程序 AppSecret。与 app-id 一起用于获取小程序 access_token 和调用小程序登录接口；当前代码直接把本值放入微信 请求
         * URI，不做非空校验，应通过环境变量等安全方式提供。
         *
         * @schemaExample ${WECHAT_MINI_APP_SECRET} | 从环境变量读取小程序 AppSecret
         */
        @Schema(description = "微信小程序 AppSecret，用于获取小程序 access_token 和调用小程序登录接口。")
        private String appSecret;
    }

    @Data
    public static class WorkProgramProperties {

        /**
         * 企业微信企业 ID（CorpId）。与 corp-secret 一起用于获取企业微信 access_token；发送消息接口不直接读取本字段。当前代码
         * 不做非空校验。
         *
         * @schemaExample ${WECHAT_WORK_CORP_ID} | 从环境变量读取企业微信 CorpId
         */
        @Schema(description = "企业微信企业 ID（CorpId），与应用 Secret 一起用于获取企业微信 access_token。")
        private String corpId;

        /**
         * 企业微信自建应用 Secret。与 corp-id 一起用于获取企业微信 access_token；当前代码不做非空校验，应通过环境变量等安全 方式提供。
         *
         * @schemaExample ${WECHAT_WORK_CORP_SECRET} | 从环境变量读取企业微信应用 Secret
         */
        @Schema(description = "企业微信自建应用 Secret，与企业 ID 一起用于获取企业微信 access_token。")
        private String corpSecret;

        /**
         * 企业微信自建应用的数值 AgentId。发送应用消息时写入请求体 agentid 字段，用于指定由哪个应用发送；它不是 Secret， 也不参与
         * access_token 获取。当前代码不做非空校验。
         */
        @Schema(description = "企业微信自建应用的数值 AgentId，发送应用消息时用于指定发送应用。")
        private Integer agentId;

    }
}
