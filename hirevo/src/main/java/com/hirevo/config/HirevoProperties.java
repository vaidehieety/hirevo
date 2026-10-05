// src/main/java/com/hirevo/config/HirevoProperties.java
package com.hirevo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Fill this record from everything under "hirevo:" in application.yml
@ConfigurationProperties(prefix = "hirevo")
// Check the rules (@Min, @NotNull...) at startup. A bad value means the app won't start.
@Validated
public record HirevoProperties(
        @Valid @NotNull Ghosting ghosting,   // the hirevo.ghosting.* group (@Valid = check the rules inside it too)
        @Valid @NotNull Ai ai) {             // the hirevo.ai.* group

    // hirevo.ghosting.max-days → maxDays. Must be at least 1 day.
    public record Ghosting(@Min(1) int maxDays) {}

    // hirevo.ai.match-threshold → between 0.0 and 1.0
    // hirevo.ai.max-roadmap-steps → at least 1 step
    public record Ai(
            @DecimalMin("0.0") @DecimalMax("1.0") double matchThreshold,
            @Min(1) int maxRoadmapSteps) {}
}