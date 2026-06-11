package com.example.notification_service.Service;


import com.example.notification_service.Repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final Logger LOGGER= LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
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
            LOGGER.error("❌ Failed to send email: {}", e.getMessage());
        }
    }

    public void sendSingleEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            LOGGER.info("✅ Email sent successfully to recipient: {}",toEmail);

        } catch (MailException e) {
            LOGGER.error("❌ Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}
