package com.example.notification_service.Service;

import com.example.notification_service.DTO.NotificationDTO;
import com.example.notification_service.Entity.Notification;
import com.example.notification_service.Entity.Role;
import com.example.notification_service.Repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(NotificationDTO notificationDTO){
        Notification notification=new Notification();
        if(notificationDTO.getRecipientType()== Role.EMPLOYEE){
            notification.setRecipientType(Role.EMPLOYEE);
            notification.setRecipientId(notificationDTO.getRecipientId());
        }
        if(notificationDTO.getRecipientType()== Role.ADMIN){
            notification.setRecipientType(Role.ADMIN);
        }
        notification.setMessage(notificationDTO.getMessage());
        Notification saved=notificationRepository.save(notification);
        System.out.println("Notification sent!!");
        return notificationRepository.save(saved);
    }
}
