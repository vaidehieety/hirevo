// src/main/java/com/hirevo/common/exception/ResourceNotFoundException.java
package com.hirevo.common.exception;

// "extends RuntimeException" makes this an UNCHECKED error:
// Java doesn't force every caller to write try/catch for it (Ch 2, p. 32).
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        // builds the message, e.g. "Job not found with id 42"
        super(resource + " not found with id " + id);
    }
}