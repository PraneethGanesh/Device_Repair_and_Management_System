package com.example.notification_service.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Who triggered the event
    @Column(nullable = false)
    private long senderId;

    @Enumerated(EnumType.STRING)
    private Role senderType;

    // Who should receive it
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role recipientType;

    private long recipientId;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public Role getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(Role recipientType) {
        this.recipientType = recipientType;
    }

    public Role getSenderType() {
        return senderType;
    }

    public void setSenderType(Role senderType) {
        this.senderType = senderType;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
