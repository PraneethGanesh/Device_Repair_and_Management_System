package com.example.notification_service.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Who triggered the event
    private String senderName;

    // Who should receive it
    @Enumerated(EnumType.STRING)
    private Role recipientType;

    private long recipientId;

    private String title;

    @Column(nullable = false)
    private String message;

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


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}

