package com.sz.socket.sever;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class ServerState {

    private volatile boolean shuttingDown = false;

}
