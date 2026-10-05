// src/main/java/com/hirevo/notification/Notification.java
package com.hirevo.notification;   // must match the folder path

// A "record" = a small, read-only data holder (Ch 2, p. 35).
// From this one line, Java writes the constructor and the getters
// to(), subject() and body() for us.
public record Notification(String to, String subject, String body) {}   