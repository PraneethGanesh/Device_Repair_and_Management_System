package com.example.notification_service.Controller;

import com.example.notification_service.DTO.NotificationDTO;
import com.example.notification_service.Entity.Notification;
import com.example.notification_service.Service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;


    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @PostMapping
    public Notification addNotification(@RequestBody NotificationDTO notificationDTO){
        return notificationService.createNotification(notificationDTO);
    }

}
