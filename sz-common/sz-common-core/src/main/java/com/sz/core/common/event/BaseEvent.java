package com.sz.core.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * @ClassName BaseEvent
 * @Author sz
 * @Date 2024/2/29 15:08
 * @Version 1.0
 */

public abstract class BaseEvent<T> extends ApplicationEvent {

    private final T payload;

    public BaseEvent(Object source, T payload) {
        super(source);
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }
}
