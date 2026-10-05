// src/main/java/com/hirevo/job/JobStatus.java
package com.hirevo.job;

// OPEN    = accepting applications
// FLAGGED = suspected fake job, waiting for an admin to review it (Fake-Job Shield, v0.4)
// CLOSED  = no longer accepting applications
public enum JobStatus { OPEN, FLAGGED, CLOSED }