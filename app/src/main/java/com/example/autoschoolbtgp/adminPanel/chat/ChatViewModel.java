package com.example.autoschoolbtgp.adminPanel.chat;

import android.util.Log;

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
    private static final String TAG = "ChatViewModel_SENIOR";

    private MutableLiveData<List<MessageModel>> messagesLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> successMessageLiveData = new MutableLiveData<>();
    // LiveData для отслеживания состояния загрузки сообщений
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    public LiveData<List<MessageModel>> getMessages() {
        return messagesLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessageLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    /**
     * Загружает список сообщений из чата
     * @param chatId ID чата
     */
    public void loadMessages(String chatId) {
        Log.d(TAG, "loadMessages: Начало загрузки сообщений из чата с chatId: " + chatId);
        isLoadingLiveData.setValue(true);

        ParseManager.getMessages(chatId, new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                isLoadingLiveData.setValue(false);
                if (e == null) {
                    Log.d(TAG, "loadMessages -> getMessages: SUCCESS. Получено " + result.size() + " сообщений из Parse.");
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
                    Log.d(TAG, "loadMessages -> getMessages: SUCCESS. Список сообщений передан в LiveData.");
                } else {
                    String errorMsg = "Ошибка загрузки сообщений: " + e.getMessage();
                    Log.e(TAG, "loadMessages -> getMessages: ERROR. " + errorMsg, e);
                    errorLiveData.setValue(errorMsg);
                }
            }
        });
    }

    /**
     * Отправляет новое сообщение в чат
     * @param chatId ID чата
     * @param text Текст сообщения
     */
    public void sendMessage(String chatId, String text) {
        Log.d(TAG, "sendMessage: Начало отправки сообщения в чат с chatId: " + chatId + ", текст: " + text);
        isLoadingLiveData.setValue(true);

        ParseManager.sendMessage(chatId, text, new FunctionCallback<Map<String, Object>>() {
            @Override
            public void done(Map<String, Object> result, ParseException e) {
                isLoadingLiveData.setValue(false);
                if (e == null) {
                    Log.d(TAG, "sendMessage -> sendMessage: SUCCESS. Сообщение успешно отправлено.");
                    successMessageLiveData.setValue("Сообщение отправлено");

                    // Обновляем список сообщений
                    loadMessages(chatId);
                } else {
                    String errorMsg = "Ошибка отправки сообщения: " + e.getMessage();
                    Log.e(TAG, "sendMessage -> sendMessage: ERROR. " + errorMsg, e);
                    errorLiveData.setValue(errorMsg);
                }
            }
        });
    }
}