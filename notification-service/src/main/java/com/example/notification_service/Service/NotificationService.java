package com.example.notification_service.Service;

import com.example.notification_service.DTO.NotificationDTO;
import com.example.notification_service.Entity.Notification;
import com.example.notification_service.Entity.NotificationStatus;
import com.example.notification_service.Entity.Role;
import com.example.notification_service.Publisher.NotificationProducer;
import com.example.notification_service.Repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;
    public NotificationService(NotificationRepository notificationRepository, NotificationProducer notificationProducer) {
        this.notificationRepository = notificationRepository;
        this.notificationProducer = notificationProducer;
    }

    public Notification createNotification(NotificationDTO notificationDTO){
        Notification notification=new Notification();
        notification.setSenderId(notificationDTO.getSenderId());
        notification.setSenderType(notificationDTO.getSenderType());
        if(notificationDTO.getRecipientType()== Role.EMPLOYEE){
            notification.setRecipientType(Role.EMPLOYEE);
            notification.setRecipientId(notificationDTO.getRecipientId());
        }
        if(notificationDTO.getRecipientType()== Role.ADMIN){
            notification.setRecipientType(Role.ADMIN);
        }
        notification.setMessage(notificationDTO.getMessage());
        notification.setNotificationStatus(NotificationStatus.PENDING);
        Notification saved=notificationRepository.save(notification);
        notificationProducer.sendMessage(saved);
        System.out.println("Notification sent!!");
        saved.setNotificationStatus(NotificationStatus.SENT);
        return notificationRepository.save(saved);
    }
}
