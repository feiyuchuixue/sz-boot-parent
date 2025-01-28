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

        // Getters and Setters
        private boolean enabled = true;

        private String locations = "classpath:db/migration";

        private boolean baselineOnMigrate = true;

        private String table = "flyway_schema_history";

        private boolean validateOnMigrate = true;

        private String baselineVersion = "1";

    }
}
