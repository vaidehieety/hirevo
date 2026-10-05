// src/main/java/com/hirevo/job/JobController.java
package com.hirevo.job;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// @RestController = a controller whose methods return DATA, written as JSON (not HTML pages)
@RestController
// Every address in this class starts with /api/v1/jobs
@RequestMapping("/api/v1/jobs")
public class JobController {

    // TEMPORARY in-memory "database": job id → job.
    // ConcurrentHashMap because many requests (threads) use this ONE controller at once (Ch 2).
    // A controller normally holds NO data. This moves out in Ch 8, and MySQL replaces it in v0.2.
    private final Map<Long, JobDto> store = new ConcurrentHashMap<>();
    // A thread-safe counter that hands out new ids: 1, 2, 3, ...
    private final AtomicLong ids = new AtomicLong();

    // GET /api/v1/jobs?skill=Java&location=Noida   (both filters are optional)
    @GetMapping
    public List<JobDto> search(@RequestParam(required = false) String skill,      // from ?skill=...
                               @RequestParam(required = false) String location) { // from ?location=...
        return store.values().stream()
                // no skill given → keep every job; otherwise keep only jobs needing that skill
                .filter(j -> skill == null || j.skills().contains(skill))
                // same idea for location, ignoring upper/lower case
                .filter(j -> location == null || j.location().equalsIgnoreCase(location))
                .toList();   // a plain List → Spring answers 200 OK with a JSON array
    }

    // GET /api/v1/jobs/42 → the {id} part of the address becomes the "id" parameter
    @GetMapping("/{id}")
    public ResponseEntity<JobDto> get(@PathVariable Long id) {
        JobDto job = store.get(id);
        // "a ? b : c" means "if a, then b, else c"
        // not found → 404 Not Found (no body); found → 200 OK with the job
        return job == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(job);
    }

    // POST /api/v1/jobs with a JSON body → @RequestBody turns that JSON into a JobDto
    @PostMapping
    public ResponseEntity<JobDto> create(@RequestBody JobDto req) {
        long id = ids.incrementAndGet();   // the SERVER chooses the id, never the client
        // The saved job: our id, the client's details, and every new job starts OPEN
        JobDto saved = new JobDto(id, req.title(), req.location(), req.workMode(), req.skills(), "OPEN");
        store.put(id, saved);
        // Build the new job's address, e.g. http://localhost:8080/api/v1/jobs/1
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();
        // 201 Created + a "Location" header pointing at the new job + the job itself
        return ResponseEntity.created(location).body(saved);
    }

    // PUT /api/v1/jobs/42 → REPLACE the whole job with what the client sent
    @PutMapping("/{id}")
    public ResponseEntity<JobDto> replace(@PathVariable Long id, @RequestBody JobDto req) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();   // nothing to replace → 404
        }
        JobDto updated = new JobDto(id, req.title(), req.location(), req.workMode(), req.skills(), req.status());
        store.put(id, updated);
        return ResponseEntity.ok(updated);              // 200 OK with the new version
    }

    // PATCH /api/v1/jobs/42/close → ONE specific change: mark the job CLOSED
    @PatchMapping("/{id}/close")
    public ResponseEntity<JobDto> close(@PathVariable Long id) {
        // computeIfPresent only runs if the id exists, and returns null if it doesn't
        JobDto j = store.computeIfPresent(id, (key, old) ->
                new JobDto(key, old.title(), old.location(), old.workMode(), old.skills(), "CLOSED"));
        return j == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(j);
    }

    // DELETE /api/v1/jobs/42
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)   // always answer 204 = "done, nothing to send back"
    public void delete(@PathVariable Long id) {
        store.remove(id);   // deleting something already gone is fine: it's still gone
    }
}