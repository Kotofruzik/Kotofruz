package com.example.autoschoolbtgp.adminPanel.chat;

public class MessageModel {
    private String id;
    private String chatId;
    private String senderId;
    private String senderName;
    private String text;
    private String createdAt;

    public MessageModel(String id, String chatId, String senderId, String senderName, String text, String createdAt) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getChatId() { return chatId; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getText() { return text; }
    public String getCreatedAt() { return createdAt; }
}