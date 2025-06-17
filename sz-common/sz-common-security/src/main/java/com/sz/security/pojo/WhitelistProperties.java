package com.sz.security.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArraySet;

@Data
@Component
@ConfigurationProperties(prefix = "router")
public class WhitelistProperties {

    private CopyOnWriteArraySet<String> whitelist = new CopyOnWriteArraySet<>();

}
