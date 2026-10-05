// src/main/java/com/hirevo/job/JobController.java
package com.hirevo.job;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hirevo.job.dto.CreateJobRequest;
import com.hirevo.job.dto.JobResponse;

import lombok.RequiredArgsConstructor;

@RestController                    // methods return data as JSON
@RequestMapping("/api/v1/jobs")    // every address here starts with /api/v1/jobs
@RequiredArgsConstructor           // Lombok: constructor for the final field → Spring injects JobService
public class JobController {

    private final JobService jobService;   // the controller's ONLY helper: the manager

    // GET /api/v1/jobs?skill=Java&location=Noida → the manager decides what to show
    @GetMapping
    public List<JobResponse> search(@RequestParam(required = false) String skill,
                                    @RequestParam(required = false) String location) {
        return jobService.search(skill, location);
    }

    // GET /api/v1/jobs/42
    @GetMapping("/{id}")
    public JobResponse get(@PathVariable Long id) {
        return jobService.getById(id);
    }

    // POST /api/v1/jobs → 201 Created + Location header
    @PostMapping
    public ResponseEntity<JobResponse> create(@RequestBody CreateJobRequest req) {
        JobResponse created = jobService.create(req);
        // the new job's address, e.g. http://localhost:8080/api/v1/jobs/1
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    // PUT /api/v1/jobs/42 → update the job's details
    @PutMapping("/{id}")
    public JobResponse update(@PathVariable Long id, @RequestBody CreateJobRequest req) {
        return jobService.update(id, req);
    }

    // PATCH /api/v1/jobs/42/close
    @PatchMapping("/{id}/close")
    public JobResponse close(@PathVariable Long id) {
        return jobService.close(id);
    }
}