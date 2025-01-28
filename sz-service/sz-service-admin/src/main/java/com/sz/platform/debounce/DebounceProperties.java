package com.sz.platform.debounce;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DebounceProperties
 * 
 * @author sz
 * @since 2024/9/18 17:19
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sz.debounce")
public class DebounceProperties {

    private boolean enabled = true;

    private long globalLockTime = 1000;

    private boolean ignoreGetMethod;

}
