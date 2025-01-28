package com.sz.core.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class BaseEvent<T> extends ApplicationEvent {

    private final T payload;

    protected BaseEvent(Object source, T payload) {
        super(source);
        this.payload = payload;
    }

}
