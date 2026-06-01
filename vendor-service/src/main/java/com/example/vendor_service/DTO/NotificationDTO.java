package com.example.vendor_service.DTO;

import com.example.vendor_service.Entity.Role;

public class NotificationDTO {
    private long senderId;
    private Role senderType;
    private Role recipientType;
    private long recipientId;
    private String message;

    public NotificationDTO(long senderId, Role senderType, Role recipientType, long recipientId, String message) {
        this.senderId = senderId;
        this.senderType = senderType;
        this.recipientType = recipientType;
        this.recipientId = recipientId;
        this.message = message;
    }

    public NotificationDTO(long senderId, Role senderType, Role recipientType, String message) {
        this.senderId = senderId;
        this.senderType = senderType;
        this.recipientType = recipientType;
        this.message = message;
    }

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
