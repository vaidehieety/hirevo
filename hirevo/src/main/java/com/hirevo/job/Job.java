// src/main/java/com/hirevo/job/Job.java
package com.hirevo.job;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// The job as OUR SYSTEM sees it (the "domain model").
// A plain class for now; in Chapter 11 it becomes a database table.
@Getter              // Lombok: writes getTitle(), getLocation()... for every field
@Setter              // Lombok: writes setTitle(...), setLocation(...)... for every field
@NoArgsConstructor   // Lombok: writes an empty constructor, public Job() {} (the database needs it later)
public class Job {
    private Long id;                 // null until saved ("Long", not "long", so it CAN be null)
    private String title;
    private String description;      // NEW: the full job description
    private String location;
    private WorkMode workMode;       // an enum now, not free text
    private int minExperience;       // in years
    private int maxExperience;
    private Integer salaryMin;       // "Integer", not "int", so it can be "not given" (null)
    private Integer salaryMax;
    private Set<String> skills = new HashSet<>();  // a Set: the same skill can't appear twice
    private JobStatus status = JobStatus.OPEN;     // every new job starts OPEN
    private Instant createdAt;       // when it was posted (the server sets this)
}