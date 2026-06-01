package com.example.repair_service.dto;

import com.example.repair_service.enums.RecipientRole;

public class NotificationDTO {

    private long senderId;
    private RecipientRole senderType;
    private RecipientRole recipientType;
    private long recipientId; // used when notifying a specific employee
    private String message;

    public NotificationDTO() {}

    public NotificationDTO(long senderId, RecipientRole senderType,
                           RecipientRole recipientType, String message) {
        this.senderId = senderId;
        this.senderType = senderType;
        this.recipientType = recipientType;
        this.message = message;
    }

    public NotificationDTO(long senderId, RecipientRole senderType,
                           RecipientRole recipientType, long recipientId, String message) {
        this.senderId = senderId;
        this.senderType = senderType;
        this.recipientType = recipientType;
        this.recipientId = recipientId;
        this.message = message;
    }

    public long getSenderId() { return senderId; }
    public void setSenderId(long senderId) { this.senderId = senderId; }

    public RecipientRole getSenderType() { return senderType; }
    public void setSenderType(RecipientRole senderType) { this.senderType = senderType; }

    public RecipientRole getRecipientType() { return recipientType; }
    public void setRecipientType(RecipientRole recipientType) { this.recipientType = recipientType; }

    public long getRecipientId() { return recipientId; }
    public void setRecipientId(long recipientId) { this.recipientId = recipientId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
