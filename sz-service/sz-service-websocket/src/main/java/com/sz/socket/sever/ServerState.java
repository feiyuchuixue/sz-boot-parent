package com.sz.socket.sever;

import org.springframework.stereotype.Component;

@Component
public class ServerState {

    private volatile boolean shuttingDown = false;

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }
}
