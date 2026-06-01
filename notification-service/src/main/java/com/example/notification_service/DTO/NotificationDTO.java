package com.example.notification_service.DTO;

import com.example.notification_service.Entity.Role;

public class NotificationDTO {
    private long senderId;
    private Role senderType;
    private Role recipientType;
    private long recipientId;
    private String message;

    public Role getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(Role recipientType) {
        this.recipientType = recipientType;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
