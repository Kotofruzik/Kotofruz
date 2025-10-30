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
    // LiveData для отслеживания состояния загрузки чатов
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    public LiveData<List<ChatModel>> getChats() {
        return chatsLiveData;
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
     * Загружает список чатов текущего пользователя
     */
    public void loadChats() {
        Log.d(TAG, "loadChats: Начало загрузки списка чатов.");
        isLoadingLiveData.setValue(true);

        ParseManager.getChatsForUser(new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                isLoadingLiveData.setValue(false);
                if (e == null) {
                    Log.d(TAG, "loadChats -> getChatsForUser: SUCCESS. Получено " + result.size() + " чатов из Parse.");
                    List<ChatModel> chats = new ArrayList<>();
                    for (Object obj : result) {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        String id = (String) map.get("id");
                        String name = (String) map.get("name");
                        String lastMessageText = (String) map.get("lastMessageText");
                        String lastMessageTime = (String) map.get("lastMessageTime");
                        String photoUrl = (String) map.get("photoUrl");
                        chats.add(new ChatModel(id, name, lastMessageText, lastMessageTime, photoUrl));
                    }
                    chatsLiveData.setValue(chats);
                    Log.d(TAG, "loadChats -> getChatsForUser: SUCCESS. Список чатов передан в LiveData.");
                } else {
                    String errorMsg = "Ошибка загрузки чатов: " + e.getMessage();
                    Log.e(TAG, "loadChats -> getChatsForUser: ERROR. " + errorMsg, e);
                    errorLiveData.setValue(errorMsg);
                }
            }
        });
    }
}