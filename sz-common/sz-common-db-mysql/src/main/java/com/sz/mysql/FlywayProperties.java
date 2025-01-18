package com.sz.mysql;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "flyway")
public class FlywayProperties {

    private FlywayConfig framework;

    private FlywayConfig business;

    @Setter
    @Getter
    public static class FlywayConfig {

        {
            enabled = true;
            locations = "classpath:db/migration";
            baselineOnMigrate = true;
            table = "flyway_schema_history";
            validateOnMigrate = true;
            baselineVersion = "1";
        }

        // Getters and Setters
        private boolean enabled;

        private String locations;

        private boolean baselineOnMigrate;

        private String table;

        private boolean validateOnMigrate;

        private String baselineVersion;

    }
}
