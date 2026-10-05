// src/main/java/com/hirevo/job/JobService.java
package com.hirevo.job;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hirevo.common.exception.ResourceNotFoundException;
import com.hirevo.job.dto.CreateJobRequest;
import com.hirevo.job.dto.JobResponse;

import lombok.RequiredArgsConstructor;

@Service                    // a bean in the business layer (the "manager")
@RequiredArgsConstructor    // Lombok writes a constructor taking the 3 "final" fields below.
                            // Spring calls it and passes in the beans: constructor injection (Ch 5)
public class JobService {

    private final JobRepository jobRepository;  // Spring passes in InMemoryJobRepository (today)
    private final JobMapper mapper;             // the translator
    private final Clock clock;                  // your Chapter 5 Clock bean

    // Create: request → Job, stamp the time, save, → response
    public JobResponse create(CreateJobRequest req) {
        Job job = mapper.toEntity(req);
        job.setCreatedAt(Instant.now(clock));   // the SERVER sets the posting time
        return mapper.toResponse(jobRepository.save(job));
    }

    // Read one job (throws "not found" if the id doesn't exist)
    public JobResponse getById(Long id) {
        return mapper.toResponse(findJob(id));
    }

    // Public search. RULE: only OPEN jobs are shown (fixes the odd thing you spotted in Ch 7!)
    public List<JobResponse> search(String skill, String location) {
        return jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == JobStatus.OPEN)                        // hide CLOSED/FLAGGED
                .filter(j -> skill == null || j.getSkills().contains(skill))
                .filter(j -> location == null || j.getLocation().equalsIgnoreCase(location))
                .map(mapper::toResponse)   // shortcut for: j -> mapper.toResponse(j)
                .toList();
    }

    // Update: load the job, copy the new values onto it, save
    public JobResponse update(Long id, CreateJobRequest req) {
        Job job = findJob(id);
        mapper.apply(job, req);
        return mapper.toResponse(jobRepository.save(job));
    }

    // Close: one specific change
    public JobResponse close(Long id) {
        Job job = findJob(id);
        job.setStatus(JobStatus.CLOSED);
        return mapper.toResponse(jobRepository.save(job));
    }

    // Shared helper: open the Optional box. Holding a job → use it; empty → throw our error
    private Job findJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", id));
    }
}