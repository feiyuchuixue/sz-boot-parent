package com.sz.core.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @since 2024-02-29
 * @author sz
 */
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(BaseEvent<?> event) {
        applicationEventPublisher.publishEvent(event);
    }

}
