// ChatsViewModel.java
package com.example.autoschoolbtgp.adminPanel.chats;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autoschoolbtgp.adminPanel.users.UserModel;
import com.example.autoschoolbtgp.utils.ParseManager;
import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsViewModel extends ViewModel {
    private MutableLiveData<List<ChatModel>> chatsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> successMessageLiveData = new MutableLiveData<>();
    // private MutableLiveData<List<MessageModel>> messagesLiveData = new MutableLiveData<>(); // <-- Убрано
    private MutableLiveData<List<UserModel>> usersLiveData = new MutableLiveData<>(); // <-- Добавлено

    public LiveData<List<ChatModel>> getChats() {
        return chatsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessageLiveData;
    }

    // public LiveData<List<MessageModel>> getMessages(){ // <-- Убрано
    //     return messagesLiveData;
    // }

    public LiveData<List<UserModel>> getUsers(){ // <-- Добавлено
        return usersLiveData;
    }

    public void loadChats() {
        ParseManager.getChatsForUser(new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                if (e == null) {
                    if (result != null) {
                        List<UserModel> users = new ArrayList<>();
                        for (Object obj : result) {
                            Map<String, Object> map = (Map<String, Object>) obj;
                            String id = (String) map.get("id");
                            String firstName = (String) map.get("firstName");
                            String lastName = (String) map.get("lastName");
                            String role = (String) map.get("role");
                            String photoUrl = (String) map.get("photoUrl");
                            users.add(new UserModel(id, firstName, lastName, role, photoUrl));
                        }
                        usersLiveData.setValue(users);
                    } else {
                        usersLiveData.setValue(new ArrayList<>()); // Передаем пустой список
                    }
                    // --- ИСПРАВЛЕНИЕ КОНЕЦ ---
                } else {
                    errorLiveData.setValue(e.getMessage());
                }
            }
        });
    }
}