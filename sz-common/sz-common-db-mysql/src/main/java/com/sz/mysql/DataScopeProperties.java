package com.sz.mysql;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "data-scope")
public class DataScopeProperties {

    {
        enable = true;
        logicMinUnit = "user";
    }

    private boolean enable;
    private String logicMinUnit;

}
