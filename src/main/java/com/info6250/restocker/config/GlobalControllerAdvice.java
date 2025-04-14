package com.info6250.restocker.config;

import com.info6250.restocker.models.Notification;
import com.info6250.restocker.services.NotificationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @Transactional
    @ModelAttribute("notifications")
    public List<Notification> getNotifications() {
        return notificationService.getUnacknowledgedNotifications();
    }
}