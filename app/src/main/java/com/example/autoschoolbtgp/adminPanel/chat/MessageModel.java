package com.example.autoschoolbtgp.adminPanel.chat;

import java.util.Date;

public class MessageModel {
    private String id;
    private String text;
    private String senderId;
    private String senderName;
    private Date createdAt;

    public MessageModel(String id, String text, String senderId, String senderName, Date createdAt) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.senderName = senderName;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getText() { return text; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public Date getCreatedAt() { return createdAt; }
}