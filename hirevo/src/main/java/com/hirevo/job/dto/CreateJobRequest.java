// src/main/java/com/hirevo/job/dto/CreateJobRequest.java
package com.hirevo.job.dto;   // note the ".dto": this file lives in the dto folder

import java.util.Set;

import com.hirevo.job.WorkMode;

// What a recruiter SENDS to create or update a job.
// Notice what's MISSING: no id, no status, no createdAt.
// The server decides those, so a client can't fake them.
public record CreateJobRequest(String title, String description, String location, WorkMode workMode,
                               int minExperience, int maxExperience,
                               Integer salaryMin, Integer salaryMax, Set<String> skills) {}