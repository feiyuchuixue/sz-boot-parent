package com.sz.mysql;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "flyway")
public class FlywayProperties {

    private FlywayConfig framework;

    private FlywayConfig business;

    public FlywayConfig getFramework() {
        return framework;
    }

    public void setFramework(FlywayConfig framework) {
        this.framework = framework;
    }

    public FlywayConfig getBusiness() {
        return business;
    }

    public void setBusiness(FlywayConfig business) {
        this.business = business;
    }

    public static class FlywayConfig {

        {
            enabled = true;
            locations = "classpath:db/migration";
            baselineOnMigrate = true;
            table = "flyway_schema_history";
            validateOnMigrate = true;
            baselineVersion = "1";
        }

        private boolean enabled;

        private String locations;

        private boolean baselineOnMigrate;

        private String table;

        private boolean validateOnMigrate;

        private String baselineVersion;

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getLocations() {
            return locations;
        }

        public void setLocations(String locations) {
            this.locations = locations;
        }

        public boolean isBaselineOnMigrate() {
            return baselineOnMigrate;
        }

        public void setBaselineOnMigrate(boolean baselineOnMigrate) {
            this.baselineOnMigrate = baselineOnMigrate;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public boolean isValidateOnMigrate() {
            return validateOnMigrate;
        }

        public void setValidateOnMigrate(boolean validateOnMigrate) {
            this.validateOnMigrate = validateOnMigrate;
        }

        public String getBaselineVersion() {
            return baselineVersion;
        }

        public void setBaselineVersion(String baselineVersion) {
            this.baselineVersion = baselineVersion;
        }
    }
}
