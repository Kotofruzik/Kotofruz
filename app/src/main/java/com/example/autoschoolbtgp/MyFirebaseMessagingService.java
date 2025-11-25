package com.example.autoschoolbtgp;

import com.example.autoschoolbtgp.adminPanel.chat.ChatManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.ParseUser;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Сохраняем токен в Back4App
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.put("deviceToken", token);
            currentUser.saveInBackground();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Проверяем, silent-ли это push
        if (remoteMessage.getData().containsKey("silent")) {
            String action = remoteMessage.getData().get("action");
            if ("new_message".equals(action)) {
                String chatId = remoteMessage.getData().get("chatId");
                // Здесь вызовем обновление чата (реализуем позже)
                ChatManager.getInstance().onNewMessageReceived(chatId);
            }
        }
    }
}