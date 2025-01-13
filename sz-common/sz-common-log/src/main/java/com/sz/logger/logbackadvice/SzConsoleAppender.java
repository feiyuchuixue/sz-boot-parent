package com.sz.logger.logbackadvice;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

/**
 * @author 柳成荫
 * @since 2021/1/9
 */
public class SzConsoleAppender extends ConsoleAppender<LoggingEvent> {

    @Override
    protected void subAppend(LoggingEvent event) {
        // 自定义操作
        DesensitizationAppender appender = new DesensitizationAppender();
        appender.operation(event); // 处理日志事件
        super.subAppend(event); // 调用父类方法，传递 LoggingEvent 类型
    }
}
