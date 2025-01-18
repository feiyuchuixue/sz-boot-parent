package com.sz.logger.logbackadvice;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

/**
 * @author 柳成荫
 * @since 2021/1/9
 */
public class SzRollingFileAppender extends RollingFileAppender<LoggingEvent> {

    @Override
    protected void subAppend(LoggingEvent event) {
        DesensitizationAppender appender = new DesensitizationAppender();
        appender.operation(event);
        super.subAppend(event);
    }
}
