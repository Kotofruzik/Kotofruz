// ChatViewModel.java
package com.example.autoschoolbtgp.adminPanel.chats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autoschoolbtgp.utils.ParseManager;
import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<List<MessageModel>> messagesLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> successMessageLiveData = new MutableLiveData<>();

    public LiveData<List<MessageModel>> getMessages() {
        return messagesLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessageLiveData;
    }

    /**
     * Загружает список сообщений из чата
     * @param chatId ID чата
     */
    public void loadMessages(String chatId) {
        ParseManager.getMessages(chatId, new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                if (e == null) {
                    List<MessageModel> messages = new ArrayList<>();
                    for (Object obj : result) {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        String id = (String) map.get("id");
                        String chatId = (String) map.get("chatId");
                        String senderId = (String) map.get("senderId");
                        String senderName = (String) map.get("senderName");
                        String text = (String) map.get("text");
                        String createdAt = (String) map.get("createdAt");
                        messages.add(new MessageModel(id, chatId, senderId, senderName, text, createdAt));
                    }
                    messagesLiveData.setValue(messages);
                } else {
                    errorLiveData.setValue(e.getMessage());
                }
            }
        });
    }

    /**
     * Отправляет сообщение в чат
     * @param chatId ID чата
     * @param text Текст сообщения
     */
    public void sendMessage(String chatId, String text) {
        ParseManager.sendMessage(chatId, text, new FunctionCallback<Map<String, Object>>() {
            @Override
            public void done(Map<String, Object> result, ParseException e) {
                if (e == null) {
                    successMessageLiveData.setValue("Сообщение отправлено");

                    // Перезагружаем сообщения
                    loadMessages(chatId);
                } else {
                    errorLiveData.setValue(e.getMessage());
                }
            }
        });
    }
}