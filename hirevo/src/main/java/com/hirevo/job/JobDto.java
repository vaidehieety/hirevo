// src/main/java/com/hirevo/job/JobDto.java
// TEMPORARY: replaced by proper request/response classes in Chapter 8
package com.hirevo.job;

import java.util.List;

// The fields of a job exactly as they appear in JSON.
// Jackson uses this record to turn incoming JSON into Java, and Java back into JSON.
public record JobDto(Long id, String title, String location, String workMode,
                     List<String> skills, String status) {}