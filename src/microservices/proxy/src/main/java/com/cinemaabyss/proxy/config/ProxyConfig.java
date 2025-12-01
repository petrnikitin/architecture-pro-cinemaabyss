package com.cinemaabyss.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {

    private String monolithUrl;
    private String moviesServiceUrl;
    private String eventsServiceUrl;
    private boolean gradualMigration;
    private int moviesMigrationPercent;
}
