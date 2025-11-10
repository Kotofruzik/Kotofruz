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

    public LiveData<List<MessageModel>> getMessages() {
        return messages;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadMessagesForUser(String targetUserId) {
        Log.d(TAG, "🔍 loadMessagesForUser: запуск загрузки для targetUserId = " + targetUserId);
        if (targetUserId == null || targetUserId.isEmpty()) {
            Log.e(TAG, "❌ loadMessagesForUser: targetUserId пустой или null!");
            error.postValue("Ошибка: ID пользователя не указан");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        ParseManager.getMessagesForUserPair(targetUserId, new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                isLoading.postValue(false);
                Log.d(TAG, "📥 getMessagesForUserPair: получен результат. Ошибка = " + e);

                if (e != null) {
                    Log.e(TAG, "❌ Ошибка Parse: " + e.getMessage(), e);
                    error.postValue("Ошибка загрузки: " + e.getMessage());
                    return;
                }

                if (result == null) {
                    Log.w(TAG, "⚠️ Результат null");
                    messages.postValue(new ArrayList<>());
                    return;
                }

                Log.d(TAG, "📊 Получено объектов: " + result.size());
                List<MessageModel> messageList = new ArrayList<>();

                for (int i = 0; i < result.size(); i++) {
                    Object obj = result.get(i);
                    Log.d(TAG, "📄 Объект " + i + ": " + obj);

                    if (!(obj instanceof Map)) {
                        Log.w(TAG, "⚠️ Объект " + i + " не является Map, пропускаем");
                        continue;
                    }

                    try {
                        Map<String, Object> map = (Map<String, Object>) obj;

                        // Логируем все ключи
                        Log.d(TAG, "🔑 Ключи объекта " + i + ": " + map.keySet());

                        String id = safeGetString(map, "id", "");
                        String text = (String) map.get("text");
                        String senderId = safeGetString(map, "senderId", "");
                        String senderName = safeGetString(map, "senderName", "[без имени]");
                        String photoUrl = safeGetString(map, "photoUrl", "");
                        Object createdAtObj = map.get("createdAt");

                        Log.d(TAG, "💬 Сообщение " + i + " -> id: " + id + ", text: '" + text + "', senderId: " + senderId + ", senderName: '" + senderName + "'");

                        // Парсим дату
                        Date createdAt = new Date();
                        if (createdAtObj instanceof String) {
                            String isoDate = (String) createdAtObj;
                            try {
                                if (isoDate.endsWith("Z")) {
                                    isoDate = isoDate.substring(0, isoDate.length() - 1) + "+0000";
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
                                createdAt = sdf.parse(isoDate);
                                Log.d(TAG, "🕒 Дата распарсена: " + createdAt);
                            } catch (Exception ex) {
                                Log.w(TAG, "⚠️ Не удалось распарсить дату: " + isoDate, ex);
                            }
                        } else {
                            Log.w(TAG, "⚠️ createdAt не строка: " + createdAtObj);
                        }

                        messageList.add(new MessageModel(id, text, senderId, senderName, createdAt));

                    } catch (Exception ex) {
                        Log.e(TAG, "💥 Ошибка парсинга сообщения " + i, ex);
                    }
                }

                Log.d(TAG, "✅ Всего обработано сообщений: " + messageList.size());
                messages.postValue(messageList);
            }
        });
    }

    public void sendMessageToUser(String targetUserId, String text) {
        Log.d(TAG, "📤 sendMessageToUser: отправка сообщения. targetUserId=" + targetUserId + ", text='" + text + "'");
        if (text == null || text.trim().isEmpty()) {
            Log.w(TAG, "⚠️ Текст сообщения пустой");
            error.setValue("Сообщение не может быть пустым");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        ParseManager.sendMessageToUser(targetUserId, text, new FunctionCallback<Map<String, Object>>() {
            @Override
            public void done(Map<String, Object> result, ParseException e) {
                isLoading.postValue(false);
                if (e == null) {
                    Log.d(TAG, "✅ Сообщение успешно отправлено");
                    successMessage.postValue("Сообщение отправлено");
                    loadMessagesForUser(targetUserId);
                } else {
                    Log.e(TAG, "❌ Ошибка отправки: " + e.getMessage(), e);
                    error.postValue("Ошибка: " + e.getMessage());
                }
            }
        });
    }

    // Вспомогательный метод для безопасного получения строки
    private String safeGetString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        Log.w(TAG, "⚠️ Ключ '" + key + "' отсутствует или не строка. Тип: " + (value != null ? value.getClass().getSimpleName() : "null"));
        return defaultValue;
    }
}