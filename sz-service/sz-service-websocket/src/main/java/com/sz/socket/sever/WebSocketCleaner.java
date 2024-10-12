package com.sz.socket.sever;

import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketCleaner implements SmartLifecycle {

    private boolean running = false;

    private final WebSocketServer webSocketServer;

    private final ServerState serverState;

    @Override
    public void start() {
        // 启动时的操作
        running = true;
    }

    @Override
    public void stop() {
        serverState.setShuttingDown(true);
        // 停止时的操作（优雅关闭）
        webSocketServer.disconnectAll();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        // 返回值越小，越先执行stop()方法
        return Integer.MAX_VALUE;
    }
}
