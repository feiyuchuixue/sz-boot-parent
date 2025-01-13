package com.sz.configuration;

import com.sz.mysql.FlywayProperties;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * FlywayConfiguration
 * 
 * @author sz
 * @since 2024/7/29 13:30
 * @version 1.0
 */
@Configuration
public class FlywayConfiguration {

    private final DataSource dataSource;

    private final FlywayProperties flywayProperties;

    public FlywayConfiguration(DataSource dataSource, FlywayProperties flywayProperties) {
        this.dataSource = dataSource;
        this.flywayProperties = flywayProperties;
    }

    @Bean
    public Flyway frameworkFlyway() {
        FlywayProperties.FlywayConfig config = flywayProperties.getFramework();
        return Flyway.configure().dataSource(dataSource).locations(config.getLocations()).table(config.getTable())
                .baselineOnMigrate(config.isBaselineOnMigrate()).validateOnMigrate(config.isValidateOnMigrate()).baselineVersion(config.getBaselineVersion())
                .load();
    }

    @Bean
    public Flyway businessFlyway() {
        FlywayProperties.FlywayConfig config = flywayProperties.getBusiness();
        return Flyway.configure().dataSource(dataSource).locations(config.getLocations()).table(config.getTable())
                .baselineOnMigrate(config.isBaselineOnMigrate()).validateOnMigrate(config.isValidateOnMigrate()).baselineVersion(config.getBaselineVersion())
                .load();
    }
}