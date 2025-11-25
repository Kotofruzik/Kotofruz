package com.example.autoschoolbtgp.adminPanel.chat;
import java.util.ArrayList;
import java.util.List;

public class ChatManager {
    private static ChatManager instance;
    private ChatCallback callback;

    public static synchronized ChatManager getInstance() {
        if (instance == null) instance = new ChatManager();
        return instance;
    }

    public void setCallback(ChatCallback callback) {
        this.callback = callback;
    }

    public void onNewMessageReceived(String chatId) {
        if (callback != null) {
            callback.onNewMessage(chatId);
        }
    }

    public interface ChatCallback {
        void onNewMessage(String chatId);
    }
}