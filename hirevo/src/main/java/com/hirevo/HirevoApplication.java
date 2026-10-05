// src/main/java/com/hirevo/HirevoApplication.java
package com.hirevo;   // the ROOT package: every feature package lives below it

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// Turns on 3 things: bean recipes, auto-configuration, and scanning com.hirevo + below
@SpringBootApplication
// NEW: also find @ConfigurationProperties records (like HirevoProperties)
@ConfigurationPropertiesScan
public class HirevoApplication {

    public static void main(String[] args) {
        // Starts Spring: creates the container, all the beans, and the embedded Tomcat server
        SpringApplication.run(HirevoApplication.class, args);
    }
}