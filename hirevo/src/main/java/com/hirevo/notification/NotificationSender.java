// src/main/java/com/hirevo/notification/NotificationSender.java
package com.hirevo.notification;

// The CONTRACT: every sender must have this method.
// It says WHAT happens ("send"), not HOW (console, email, SMS...).
public interface NotificationSender {
    void send(Notification notification);
}