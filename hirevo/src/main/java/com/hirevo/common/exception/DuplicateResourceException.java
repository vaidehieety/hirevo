// src/main/java/com/hirevo/common/exception/DuplicateResourceException.java
package com.hirevo.common.exception;

// "Something unique already exists" → becomes 409 Conflict.
// First used in v0.2: applying to the same job twice, or registering an email twice.
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);   // e.g. "You have already applied to this job"
    }
}