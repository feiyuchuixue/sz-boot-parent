package com.sz.logger.logbackadvice;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

/**
 * @ClassName ConsoleAppenderDS
 * @Description
 * @Author 柳成荫
 * @Date 2021/1/9
 */
public class SzConsoleAppender extends ConsoleAppender {

    @Override
    protected void subAppend(Object event) {
        DesensitizationAppender appender = new DesensitizationAppender();
        appender.operation((LoggingEvent) event);
        super.subAppend(event);
    }
}
