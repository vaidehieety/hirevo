// src/main/java/com/hirevo/notification/ConsoleNotificationSender.java
package com.hirevo.notification;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("dev")   // NEW: only create this bean when the "dev" profile is active
@Slf4j            // Lombok writes the "log" variable for us
public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public void send(Notification n) {
        // During development we print the message instead of emailing it.
        log.info("[DEV EMAIL] to={} subject={} body={}", n.to(), n.subject(), n.body());
    }
}