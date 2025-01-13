package com.sz.wechat;

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

    // 小程序开发者配置
    private MiniProgramProperties mini;

    @Data
    public static class MiniProgramProperties {

        private String appId;

        private String appSecret;

    }

}
