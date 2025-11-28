package com.example.autoschoolbtgp.adminPanel.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autoschoolbtgp.utils.ParseManager;
import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "ChatViewModel_DEBUG";

    private MutableLiveData<List<MessageModel>> messages = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<List<MessageModel>> getMessages() { return messages; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // Загрузка по targetUserId (как раньше)
    public void loadMessagesForUser(String targetUserId) {
        if (targetUserId == null || targetUserId.isEmpty()) {
            error.setValue("ID пользователя не указан");
            return;
        }

        isLoading.setValue(true);
        ParseManager.getMessagesForUserPair(targetUserId, new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                isLoading.setValue(false);
                if (e != null) {
                    error.setValue("Ошибка загрузки: " + e.getMessage());
                    return;
                }

                List<MessageModel> messageList = new ArrayList<>();
                if (result != null) {
                    for (Object obj : result) {
                        if (obj instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) obj;
                            String id = safeGetString(map, "id", "");
                            String text = (String) map.get("text");
                            String senderId = safeGetString(map, "senderId", "");
                            String senderName = safeGetString(map, "senderName", "[без имени]");
                            Date createdAt = parseDate(map.get("createdAt"));
                            messageList.add(new MessageModel(id, text, senderId, senderName, createdAt));
                        }
                    }
                }
                messages.setValue(messageList);
            }
        });
    }

    // Отправка по targetUserId
    public void sendMessageToUser(String targetUserId, String text) {
        if (text == null || text.trim().isEmpty()) {
            error.setValue("Сообщение не может быть пустым");
            return;
        }

        isLoading.setValue(true);
        ParseManager.sendMessageToUser(targetUserId, text, new FunctionCallback<Map<String, Object>>() {
            @Override
            public void done(Map<String, Object> result, ParseException e) {
                isLoading.setValue(false);
                if (e == null) {
                    successMessage.setValue("Сообщение отправлено");
                    loadMessagesForUser(targetUserId); // перезагрузка
                } else {
                    error.setValue("Ошибка: " + e.getMessage());
                }
            }
        });
    }

    private String safeGetString(Map<String, Object> map, String key, String def) {
        Object val = map.get(key);
        return (val instanceof String) ? (String) val : def;
    }

    private Date parseDate(Object dateObj) {
        if (dateObj instanceof String) {
            try {
                String iso = ((String) dateObj).replace("Z", "+0000");
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).parse(iso);
            } catch (Exception e) {
                return new Date();
            }
        }
        return new Date();
    }
}