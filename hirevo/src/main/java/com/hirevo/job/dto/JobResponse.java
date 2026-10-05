// src/main/java/com/hirevo/job/dto/JobResponse.java
package com.hirevo.job.dto;

import java.time.Instant;
import java.util.Set;

import com.hirevo.job.JobStatus;
import com.hirevo.job.WorkMode;

// What we SEND BACK: the job, including the fields the server decided (id, status, createdAt).
public record JobResponse(Long id, String title, String description, String location, WorkMode workMode,
                          int minExperience, int maxExperience, Integer salaryMin, Integer salaryMax,
                          Set<String> skills, JobStatus status, Instant createdAt) {}