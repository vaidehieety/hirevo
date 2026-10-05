// src/main/java/com/hirevo/job/dto/CreateJobRequest.java
package com.hirevo.job.dto;

import java.util.Set;

import com.hirevo.common.validation.ValidRanges;
import com.hirevo.job.WorkMode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// What a recruiter SENDS. No id/status/createdAt: the server decides those.
@ValidRanges   // our two-field rule: min ≤ max for salary and experience
public record CreateJobRequest(
        @NotBlank @Size(max = 120) String title,                  // required, max 120 characters
        @NotBlank @Size(min = 50, max = 5000) String description, // a REAL description: at least 50 characters
        @NotBlank @Size(max = 80) String location,
        @NotNull WorkMode workMode,                               // must be REMOTE, ONSITE or HYBRID
        @Min(0) @Max(30) int minExperience,                       // in years
        @Min(0) @Max(40) int maxExperience,
        @PositiveOrZero Integer salaryMin,                        // optional, but never negative
        @PositiveOrZero Integer salaryMax,
        // 1 to 15 skills, each one non-blank and at most 40 characters
        @NotEmpty @Size(max = 15) Set<@NotBlank @Size(max = 40) String> skills) {}