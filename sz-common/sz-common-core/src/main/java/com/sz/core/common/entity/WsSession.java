package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;



/**
 * @author sz
 * @date 2023/9/8 8:32
 */
@Data
public class WsSession {

    @Schema(description = "sid,自定义生成的uuid")
    private String sid;

    @Schema(description = "username")
    private String username;

    @Schema(description = "socket-session")
    private WebSocketSession session;

    public WsSession(String sid, String username, WebSocketSession session) {
        this.sid = sid;
        this.username = username;
        this.session = session;
    }
}
