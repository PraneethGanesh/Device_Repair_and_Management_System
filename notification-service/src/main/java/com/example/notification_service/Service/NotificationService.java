package com.example.notification_service.Service;

import com.example.notification_service.DTO.NotificationDTO;
import com.example.notification_service.DTO.NotificationMessage;
import com.example.notification_service.DTO.UserDTO;
import com.example.notification_service.Entity.Notification;
import com.example.notification_service.Entity.Role;
import com.example.notification_service.Repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.mail.MailException;
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

    private static final Logger LOGGER= LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationRepository notificationRepository, @LoadBalanced RestClient.Builder userClientBuilder, JavaMailSender mailSender) {
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
                   .body(new ParameterizedTypeReference<List<UserDTO>>() {});
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
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmails.toArray(new String[0]));
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            LOGGER.info("✅ Email sent successfully to {} recipients: {}", toEmails.size(), toEmails);

        } catch (MailException e) {
            LOGGER.error("❌ Failed to send email to {}: {}", toEmails, e.getMessage());
        }
    }
}
