// src/main/java/com/hirevo/common/exception/GlobalExceptionHandler.java
package com.hirevo.common.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

// @RestControllerAdvice = "watch ALL controllers; when one throws an error, bring it here"
@RestControllerAdvice
@Slf4j   // Lombok: gives us "log" for writing to the app's logs
// Extending ResponseEntityExceptionHandler gives us correct handling of Spring's OWN errors
// for free: unknown URL → 404, broken JSON → 400, wrong method → 405, all as ProblemDetail
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // "When a ResourceNotFoundException is thrown anywhere..."
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        // "...answer 404, using the error's own message as the detail"
        return problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        return problem(HttpStatus.CONFLICT, "Already exists", ex.getMessage());                     // 409
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleRule(BusinessRuleException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Request not allowed", ex.getMessage());    // 422
    }

    // READY FOR PART B: when field validation fails (e.g. a blank title),
    // build a 400 that lists every bad field: { "title": "must not be blank", ... }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();   // keeps the order the errors were found
        ex.getBindingResult().getFieldErrors()                // errors on single fields
                .forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors()               // errors on the whole object (Part B's custom rule)
                .forEach(e -> errors.putIfAbsent(e.getObjectName(), e.getDefaultMessage()));
        ProblemDetail pd = ex.getBody();                      // Spring's prepared 400 reply
        pd.setTitle("Validation failed");
        pd.setDetail("One or more fields are invalid");
        pd.setProperty("errors", errors);                     // add our field → message list
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.badRequest().body(pd);
    }

    // LAST LINE OF DEFENCE: any error nobody else handled is a BUG
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);   // the full details go to OUR logs, for us to fix...
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error",
                "Something went wrong. Please try again later.");   // ...the client gets a polite message
    }

    // Small helper so every error reply looks the same
    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}