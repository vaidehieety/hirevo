// src/main/java/com/hirevo/job/JobMapper.java
package com.hirevo.job;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.hirevo.job.dto.CreateJobRequest;
import com.hirevo.job.dto.JobResponse;

@Component   // a bean, so the service can ask Spring for it
public class JobMapper {

    // CreateJobRequest (what the client sent) → a brand-new Job
    public Job toEntity(CreateJobRequest r) {
        Job job = new Job();
        apply(job, r);   // reuse the same copying code as "update"
        return job;
    }

    // Copy the request's fields onto an existing Job (used by create AND update)
    public void apply(Job job, CreateJobRequest r) {
        job.setTitle(r.title());
        job.setDescription(r.description());
        job.setLocation(r.location());
        job.setWorkMode(r.workMode());
        job.setMinExperience(r.minExperience());
        job.setMaxExperience(r.maxExperience());
        job.setSalaryMin(r.salaryMin());
        job.setSalaryMax(r.salaryMax());
        // If the client sent no skills, use an empty set instead of null (avoids crashes later)
        Set<String> skills = r.skills() == null ? Set.of() : r.skills();
        job.setSkills(new HashSet<>(skills));   // our own copy, so the client's set can't change ours
    }

    // Job → JobResponse (what the client gets to see)
    public JobResponse toResponse(Job j) {
        return new JobResponse(j.getId(), j.getTitle(), j.getDescription(), j.getLocation(),
                j.getWorkMode(), j.getMinExperience(), j.getMaxExperience(),
                j.getSalaryMin(), j.getSalaryMax(),
                Set.copyOf(j.getSkills()),   // a read-only copy: the response can't change our Job
                j.getStatus(), j.getCreatedAt());
    }
}