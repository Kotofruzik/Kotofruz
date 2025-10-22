package com.example.autoschoolbtgp.adminPanel.chats;

import java.util.List;

public class ChatModel {
    private String id;
    private String name;
    private List<String> members;
    private String lastMessage;
    private String photo;

    public ChatModel(String id, String name, List<String> members) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.lastMessage = lastMessage;
        this.photo = photo;
    }

    public String getId() {return id;}
    public String getName() {return name;}
    public List<String> getMembers() {return members;}
    public String getLastMessage() {return lastMessage;}
    public String getPhoto() {return photo;}

    public void setName(String name) {this.name = name;}
    public void setLastMessage(String lastMessage) {this.lastMessage = lastMessage;}
    public void setPhoto(String photo) {this.photo = photo;}
}
