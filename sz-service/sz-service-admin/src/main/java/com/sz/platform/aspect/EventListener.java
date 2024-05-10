package com.sz.platform.aspect;

import com.sz.platform.common.annotation.CacheFlush;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EventListener {

    @After("@annotation(cacheFlush)")
    public void afterEventTrigger(CacheFlush cacheFlush) {
        String eventName = cacheFlush.eventName();
        System.out.println("Event triggered: " + eventName);
    }
}
