// src/main/java/com/hirevo/common/exception/BusinessRuleException.java
package com.hirevo.common.exception;

// "A valid request that breaks a rule" → becomes 422 Unprocessable Entity.
// First used in v0.2: applying to a CLOSED job, or an invalid status change.
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);   // e.g. "This job is closed"
    }
}