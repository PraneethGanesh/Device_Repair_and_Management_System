package com.example.user_service.dto;

import com.example.user_service.entity.NotificationStatus;
import com.example.user_service.entity.Role;

public class NotificationMessage {
    private long id;
    private long senderId;
    private Role senderType;
    private Role recipientType;
    private long recipientId;     // populated for EMPLOYEE, null for ADMIN
    private String message;
    private NotificationStatus notificationStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public Role getSenderType() {
        return senderType;
    }

    public void setSenderType(Role senderType) {
        this.senderType = senderType;
    }

    public Role getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(Role recipientType) {
        this.recipientType = recipientType;
    }
}
