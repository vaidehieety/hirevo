// src/main/java/com/hirevo/notification/ConsoleNotificationSender.java
package com.hirevo.notification;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// @Component = "Spring, create ONE object of this class and manage it" → it becomes a bean.
// At startup, Spring scans com.hirevo and everything below it, and finds this label.
@Component
// @Slf4j = Lombok writes a "log" variable for us, used for printing log messages.
@Slf4j
public class ConsoleNotificationSender implements NotificationSender {  // keeps the contract's promise

    // @Override = "this method fulfils the interface's send()"
    @Override
    public void send(Notification n) {
        // During development we just print the message instead of emailing it.
        // Each {} is filled with the next value, in order.
        log.info("[DEV EMAIL] to={} subject={} body={}", n.to(), n.subject(), n.body());
    }
}