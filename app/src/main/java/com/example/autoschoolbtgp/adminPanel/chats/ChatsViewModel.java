package com.example.autoschoolbtgp.adminPanel.chats;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autoschoolbtgp.utils.ParseManager;
import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatsViewModel extends ViewModel {
    private static final String TAG = "ChatsViewModel_SENIOR";

    private MutableLiveData<List<ChatModel>> chats = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<List<ChatModel>> getChats() {
        return chats;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Загружает список чатов для текущего пользователя
     */
    public void loadChats() {
        isLoading.setValue(true);
        error.setValue(null);

        ParseManager.getChatsForUser(new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                isLoading.postValue(false);
                if (e == null && result != null) {
                    List<ChatModel> chatList = new ArrayList<>();
                    for (Object obj : result) {
                        try {
                            Map<String, Object> map = (Map<String, Object>) obj;
                            String id = (String) map.get("id");
                            String senderId = (String) map.get("senderId");
                            String receiverId = (String) map.get("receiverId");
                            String name = (String) map.get("name");
                            String lastMessageText = (String) map.get("lastMessageText");
                            String lastMessageTime = (String) map.get("lastMessageTime");
                            String photoUrl = (String) map.get("photoUrl");

                            chatList.add(new ChatModel(
                                    id,
                                    senderId,
                                    receiverId,
                                    name != null ? name : "Без имени",
                                    lastMessageText,
                                    lastMessageTime,
                                    photoUrl
                            ));
                        } catch (Exception ex) {
                            Log.e(TAG, "Ошибка парсинга чата: " + ex.getMessage(), ex);
                        }
                    }
                    chats.postValue(chatList);
                } else {
                    String errorMsg = e != null ? e.getMessage() : "Неизвестная ошибка";
                    Log.e(TAG, "Ошибка загрузки чатов: " + errorMsg);
                    error.postValue(errorMsg);
                }
            }
        });
    }
}