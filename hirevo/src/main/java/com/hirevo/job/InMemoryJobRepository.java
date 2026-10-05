// src/main/java/com/hirevo/job/InMemoryJobRepository.java
// TEMPORARY: deleted in Chapter 11, when MySQL takes over
package com.hirevo.job;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

// @Repository = a bean in the data layer (the "records room")
// No "public" before class: only code inside the job package can see it.
// Everyone else must go through the JobRepository contract.
@Repository
class InMemoryJobRepository implements JobRepository {

    private final Map<Long, Job> store = new ConcurrentHashMap<>();  // thread-safe map: id → job
    private final AtomicLong ids = new AtomicLong();                 // thread-safe counter: 1, 2, 3...

    @Override
    public Job save(Job job) {
        if (job.getId() == null) {             // a new job has no id yet...
            job.setId(ids.incrementAndGet());  // ...so give it the next one
        }
        store.put(job.getId(), job);           // insert, or overwrite if it already exists
        return job;
    }

    @Override
    public Optional<Job> findById(Long id) {
        // Optional.ofNullable: a box holding the job, or an empty box if the map had nothing
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Job> findAll() {
        return List.copyOf(store.values());    // a copy, so nobody outside can change our store
    }
}