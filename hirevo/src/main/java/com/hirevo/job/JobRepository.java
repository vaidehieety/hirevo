// src/main/java/com/hirevo/job/JobRepository.java
package com.hirevo.job;

import java.util.List;
import java.util.Optional;

// The CONTRACT for storing jobs. JobService only ever talks to this interface.
public interface JobRepository {

    Job save(Job job);                // add a new job or update an existing one; returns it

    Optional<Job> findById(Long id);  // Optional = a box that may be EMPTY (no job with that id)

    List<Job> findAll();              // every job
}