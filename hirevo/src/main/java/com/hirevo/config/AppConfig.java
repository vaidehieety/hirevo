// src/main/java/com/hirevo/config/AppConfig.java
package com.hirevo.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration = "this class holds bean recipes"
@Configuration
public class AppConfig {

    // @Bean = "Spring, run this method once at startup and keep what it returns as a bean"
    @Bean
    public Clock clock() {
        // The real clock, in UTC (world standard time). Servers store times in UTC.
        // Later, tests will swap in a frozen clock with Clock.fixed(...).
        return Clock.systemUTC();
    }
}