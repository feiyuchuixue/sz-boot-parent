package com.sz.socket.configuration;

import com.sz.socket.sever.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketServer webSocketServer;

    private final WebSocketInterceptor webSocketInterceptor;

    /**
     * 允许跨域的 origin 列表，逗号分隔； 默认 "*" 仅用于本地开发，生产请显式配置具体域名。
     */
    @Value("${sz.cors.allowed-origins:*}")
    private String allowedOrigins;

    public WebSocketConfig(WebSocketServer webSocketServer, WebSocketInterceptor webSocketInterceptor) {
        this.webSocketServer = webSocketServer;
        this.webSocketInterceptor = webSocketInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        String[] origins = parseOrigins(allowedOrigins);
        if (origins.length == 1 && "*".equals(origins[0])) {
            log.warn("【websocket】CORS 配置为 \"*\"，生产环境建议显式指定 sz.cors.allowed-origins");
        } else {
            log.info("【websocket】CORS allowedOrigins = {}", String.join(",", origins));
        }
        webSocketHandlerRegistry.addHandler(webSocketServer, "/socket").setAllowedOrigins(origins).addInterceptors(webSocketInterceptor);
    }

    private String[] parseOrigins(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return new String[]{"*"};
        }
        String[] parts = raw.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

}
