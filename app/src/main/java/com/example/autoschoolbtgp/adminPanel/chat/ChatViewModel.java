package com.example.autoschoolbtgp.adminPanel.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autoschoolbtgp.utils.ParseManager;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

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

    // chatId в формате "user1_user2" — уникальный идентификатор чата
    private String currentChatId;

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

    // 🔻 ЗАГРУЗКА СООБЩЕНИЙ ДЛЯ ЧАТА (по chatId)
    public void loadMessagesForChat(String chatId) {
        Log.d(TAG, "🔍 loadMessagesForChat: загрузка для chatId = " + chatId);
        if (chatId == null || chatId.isEmpty()) {
            Log.e(TAG, "❌ chatId пустой или null!");
            error.postValue("Ошибка: ID чата не указан");
            return;
        }

        this.currentChatId = chatId;
        isLoading.setValue(true);
        error.setValue(null);

        ParseManager.getMessagesForChat(chatId, new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                isLoading.postValue(false);
                Log.d(TAG, "📥 getMessagesForChat: результат получен. Ошибка = " + e);

                if (e != null) {
                    Log.e(TAG, "❌ Ошибка Parse: " + e.getMessage(), e);
                    error.postValue("Ошибка загрузки: " + e.getMessage());
                    return;
                }

                if (result == null) {
                    Log.w(TAG, "⚠️ Результат null — устанавливаем пустой список");
                    messages.postValue(new ArrayList<>());
                    return;
                }

                Log.d(TAG, "📊 Получено объектов: " + result.size());
                List<MessageModel> messageList = new ArrayList<>();

                for (int i = 0; i < result.size(); i++) {
                    Object obj = result.get(i);
                    Log.d(TAG, "📄 Объект " + i + ": " + obj);

                    if (!(obj instanceof Map)) {
                        Log.w(TAG, "⚠️ Объект " + i + " не Map — пропускаем");
                        continue;
                    }

                    try {
                        Map<String, Object> map = (Map<String, Object>) obj;

                        String id = safeGetString(map, "id", "");
                        String text = (String) map.get("text");
                        String senderId = safeGetString(map, "senderId", "");
                        String senderName = safeGetString(map, "senderName", "[без имени]");
                        Object createdAtObj = map.get("createdAt");

                        Date createdAt = new Date();
                        if (createdAtObj instanceof String) {
                            String isoDate = (String) createdAtObj;
                            try {
                                if (isoDate.endsWith("Z")) {
                                    isoDate = isoDate.substring(0, isoDate.length() - 1) + "+0000";
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
                                createdAt = sdf.parse(isoDate);
                            } catch (Exception ex) {
                                Log.w(TAG, "⚠️ Не удалось распарсить дату: " + isoDate, ex);
                            }
                        }

                        messageList.add(new MessageModel(id, text, senderId, senderName, createdAt));

                    } catch (Exception ex) {
                        Log.e(TAG, "💥 Ошибка парсинга сообщения " + i, ex);
                    }
                }

                Log.d(TAG, "✅ Обработано сообщений: " + messageList.size());
                messages.postValue(messageList);
            }
        });
    }

    // 🔻 ОТПРАВКА СООБЩЕНИЯ В ЧАТ (chatId + recipientId для Cloud Code)
    public void sendMessageToChat(String chatId, String text, String recipientId) {
        Log.d(TAG, "📤 sendMessageToChat: chatId=" + chatId + ", recipientId=" + recipientId);
        if (text == null || text.trim().isEmpty()) {
            error.setValue("Сообщение не может быть пустым");
            return;
        }

        if (chatId == null || recipientId == null) {
            error.setValue("Ошибка: не хватает данных для отправки");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        ParseManager.sendMessageToChat(chatId, recipientId, text, new FunctionCallback<Map<String, Object>>() {
            @Override
            public void done(Map<String, Object> result, ParseException e) {
                isLoading.postValue(false);
                if (e == null) {
                    Log.d(TAG, "✅ Сообщение успешно отправлено в Parse");
                    successMessage.postValue("Сообщение отправлено");
                    // Optional: можно сразу добавить в список, но лучше ждать push
                } else {
                    Log.e(TAG, "❌ Ошибка отправки: " + e.getMessage(), e);
                    error.postValue("Ошибка: " + e.getMessage());
                }
            }
        });
    }

    private String safeGetString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        Log.w(TAG, "⚠️ Ключ '" + key + "' отсутствует или не строка. Тип: " + (value != null ? value.getClass().getSimpleName() : "null"));
        return defaultValue;
    }

    // onCleared остаётся пустым — нет сокетов
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "🧹 ChatViewModel очищен");
    }
}