package com.example.autoschoolbtgp.adminPanel.chats;

public class ChatModel {
    private String id;
    private String senderId;
    private String receiverId;
    private String name;
    private String lastMessageText;
    private String lastMessageTime;
    private String photoUrl;

    public ChatModel(String id, String senderId, String receiverId, String name, String lastMessageText, String lastMessageTime, String photoUrl) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.name = name;
        this.lastMessageText = lastMessageText;
        this.lastMessageTime = lastMessageTime;
        this.photoUrl = photoUrl;
    }

    // Getters
    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }

    public String getName() { return name; }
    public String getLastMessageText() { return lastMessageText; }
    public String getLastMessageTime() { return lastMessageTime; }
    public String getPhotoUrl() { return photoUrl; }

    // Setters
    public void setLastMessageText(String lastMessageText) { this.lastMessageText = lastMessageText; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}