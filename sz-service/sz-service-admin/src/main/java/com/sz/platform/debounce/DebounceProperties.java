package com.sz.platform.debounce;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DebounceProperties
 * @Author sz
 * @Date 2024/9/18 17:19
 * @Version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sz.debounce")
public class DebounceProperties {

    {
        enabled = true;
        globalLockTime = 1000;
    }

    private boolean enabled;

    private long globalLockTime;

    private boolean ignoreGetMethod;

}
