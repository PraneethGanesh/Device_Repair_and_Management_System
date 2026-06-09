package com.example.notification_service.Service;

import com.example.notification_service.DTO.NotificationDTO;
import com.example.notification_service.DTO.NotificationMessage;
import com.example.notification_service.DTO.UserDTO;
import com.example.notification_service.Entity.Notification;
import com.example.notification_service.Entity.Role;
import com.example.notification_service.Repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final RestClient userClient;
    public NotificationService(NotificationRepository notificationRepository, RestClient.Builder userClientBuilder) {
        this.notificationRepository = notificationRepository;
        this.userClient = userClientBuilder.baseUrl("http://user-service").build();
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

    public void sendEmail(NotificationMessage notificationMessage) {
        if(notificationMessage.getReceiverType().equals("COMPANY_ADMIN")){
           List<UserDTO> userDTOS=userClient.get()
                   .uri("/api/users/{role}",notificationMessage.getReceiverType())
                   .retrieve()
                   .body(List.class);

        }
    }
}
