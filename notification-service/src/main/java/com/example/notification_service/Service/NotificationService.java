package com.example.notification_service.Service;

import com.example.notification_service.DTO.NotificationDTO;
import com.example.notification_service.DTO.NotificationMessage;
import com.example.notification_service.DTO.UserDTO;
import com.example.notification_service.Entity.Notification;
import com.example.notification_service.Entity.Role;
import com.example.notification_service.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final RestClient userClient;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public NotificationService(NotificationRepository notificationRepository, RestClient.Builder userClientBuilder, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.userClient = userClientBuilder.baseUrl("http://user-service").build();
        this.mailSender = mailSender;
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

    public void sendMessage(NotificationMessage notificationMessage) {
        String subject;
        String body;
        if(notificationMessage.getReceiverType().equals("COMPANY_ADMIN")){
           List<UserDTO> userDTOS=userClient.get()
                   .uri("/api/users/{role}",notificationMessage.getReceiverType())
                   .retrieve()
                   .body(List.class);
            List<String> adminEmails=userDTOS.stream().map(userDTO ->userDTO.getEmail()).toList();
            subject= notificationMessage.getTitle();
            body=String.format(
                    "Hello Admin,\n\n" +
                            "A new device has been added by a vendor.\n\n" +
                            "Device Details: %s\n" +
                            "Please log in to the Device Supply Management System to know more.\n\n" +
                            "Regards,\n" +
                            "Device Supply Management System",
                    notificationMessage.getMessage()
            );
            sendBulkEmail(adminEmails,subject,body);

        }

    }

    public void sendBulkEmail(List<String> toEmails, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmails.toArray(new String[0]));  // all admins in one call
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);  // single SMTP call, not a loop
    }
}
