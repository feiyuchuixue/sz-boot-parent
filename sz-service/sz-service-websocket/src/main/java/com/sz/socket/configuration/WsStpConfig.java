package com.sz.socket.configuration;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WS 服务 sa-token 配置。
 * <p>
 * 注册与 sz-common-security（admin 服务）相同的 {@link StpLogicJwtForSimple}， 保证两端对 JWT
 * token 的解析、active-timeout 查询逻辑完全一致。 若不注册，WS 服务默认使用普通 {@link StpLogic}（UUID
 * 模式）， 会因 active-timeout key 格式差异导致 {@code getLoginIdByToken} 返回 null。
 *
 * @author sz
 */
@Configuration
public class WsStpConfig {

    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

}
