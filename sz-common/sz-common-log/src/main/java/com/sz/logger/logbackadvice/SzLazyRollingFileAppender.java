package com.sz.logger.logbackadvice;

import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * 延迟创建日志文件的滚动 Appender。
 * <p>
 * 用于低频分类日志，避免服务启动时生成无内容的审计、诊断日志文件。
 */
public class SzLazyRollingFileAppender extends SzRollingFileAppender {

    private final ThreadLocal<Boolean> appending = ThreadLocal.withInitial(() -> false);

    private volatile boolean delegateStarted;

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        if (delegateStarted) {
            super.stop();
            delegateStarted = false;
            return;
        }
        this.started = false;
    }

    @Override
    public void doAppend(LoggingEvent event) {
        if (!isStarted() || event == null || Boolean.TRUE.equals(appending.get())) {
            return;
        }
        try {
            appending.set(true);
            ensureDelegateStarted();
            if (delegateStarted) {
                super.subAppend(event);
            }
        } catch (Exception e) {
            addError("Failed to append lazy rolling log event.", e);
        } finally {
            appending.set(false);
        }
    }

    private void ensureDelegateStarted() {
        if (delegateStarted) {
            return;
        }
        synchronized (this) {
            if (delegateStarted) {
                return;
            }
            this.started = false;
            super.start();
            delegateStarted = isStarted();
        }
    }
}
