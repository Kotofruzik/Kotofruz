package com.example.autoschoolbtgp.adminPanel.chats;

public class ChatListModel {
    private String id;
    private String name;
    private String lastMessageText;
    private String lastMessageTime;
    private String photoUrl;

    public ChatListModel(String id, String name, String lastMessageText, String lastMessageTime, String photoUrl) {
        this.id = id;
        this.name = name;
        this.lastMessageText = lastMessageText;
        this.lastMessageTime = lastMessageTime;
        this.photoUrl = photoUrl;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLastMessageText() { return lastMessageText; }
    public String getLastMessageTime() { return lastMessageTime; }
    public String getPhotoUrl() { return photoUrl; }

    // Setters (если нужно)
    public void setLastMessageText(String lastMessageText) { this.lastMessageText = lastMessageText; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}