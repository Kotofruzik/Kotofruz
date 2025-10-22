package com.example.autoschoolbtgp.adminPanel.chats;

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

public class ChatsViewModel extends ViewModel {
    private static final String TAG = "ChatsViewModel_SENIOR";

    private MutableLiveData<List<ChatModel>> chatsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> successMessageLiveData = new MutableLiveData<>();

    public LiveData<List<ChatModel>> getChats() {
        return chatsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessageLiveData;
    }

    /**
     * Загружает список чатов текущего пользователя.
     */
    public void loadChats() {
        Log.d(TAG, "loadChats: Начало загрузки списка чатов.");
        ParseManager.getChatsForUser(new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "loadChats: Получено " + result.size() + " чатов из Parse.");
                    List<ChatModel> chats = new ArrayList<>();
                    for (Object obj : result) {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        String id = (String) map.get("id");
                        String name = (String) map.get("name");
                        List<String> members = (List<String>) map.get("members");
                        String lastMessage = (String) map.get("lastMessage"); // Если есть
                        String photoUrl = (String) map.get("photoUrl"); // Если есть
                        chats.add(new ChatModel(id, name, members, lastMessage, photoUrl));
                    }
                    chatsLiveData.setValue(chats);
                    Log.d(TAG, "loadChats: Список чатов передан в LiveData.");
                } else {
                    String errorMsg = "Ошибка загрузки чатов: " + e.getMessage();
                    Log.e(TAG, "loadChats: " + errorMsg, e);
                    errorLiveData.setValue(errorMsg);
                }
            }
        });
    }

    /**
     * Открывает чат с пользователем.
     * @param targetUserId ID пользователя, с которым открывается чат.
     */
    public void openChatWithUser(String targetUserId) {
        Log.d(TAG, "openChatWithUser: Начало открытия чата с userId: " + targetUserId);
        ParseManager.getOrCreateChat(targetUserId, new FunctionCallback<Map<String, Object>>() {
            @Override
            public void done(Map<String, Object> result, ParseException e) {
                if (e == null) {
                    String chatId = (String) result.get("chatId");
                    String name = (String) result.get("name");
                    Log.d(TAG, "openChatWithUser: Чат успешно создан/найден. chatId: " + chatId + ", name: " + name);
                    successMessageLiveData.setValue("Чат открыт: " + name);

                    // Здесь можно отправить событие в LiveData, чтобы фрагмент открыл чат
                    // Например, через SingleLiveEvent или аналог
                    // openChatEvent.setValue(chatId);
                } else {
                    String errorMsg = "Ошибка открытия чата: " + e.getMessage();
                    Log.e(TAG, "openChatWithUser: " + errorMsg, e);
                    errorLiveData.setValue(errorMsg);
                }
            }
        });
    }

    public void logout() {
        Log.d(TAG, "logout: Выход пользователя из системы.");
        ParseManager.logout();
        Log.d(TAG, "logout: Вызов ParseManager.logout() завершён.");
    }
}