package com.example.autoschoolbtgp.adminPanel.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.autoschoolbtgp.utils.ParseManager;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<List<UserModel>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> successMessageLiveData = new MutableLiveData<>();


    public LiveData<List<UserModel>> getUsers() {
        return usersLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }
    public LiveData<String> getSuccessMessage() {
        return successMessageLiveData;
    }


    public void loadUsers() {
        ParseManager.getAllUsers(new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                if (e == null) {
                    List<UserModel> users = new ArrayList<>();
                    String currentUserId = ParseUser.getCurrentUser().getObjectId(); // Получаем ID текущего пользователя

                    for (Object obj : result) {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        String id = (String) map.get("id");

                        // Пропускаем текущего пользователя
                        if (id.equals(currentUserId)) {
                            continue;
                        }

                        String firstName = (String) map.get("firstName");
                        String lastName = (String) map.get("lastName");
                        String role = (String) map.get("role");
                        String photo = (String) map.get("photo");

                        // Пропускаем пользователя с именем "kotofruzik"
                        if ("kotofruzik".equals(firstName) || "kotofruzik".equals(lastName)) {
                            continue;
                        }

                        users.add(new UserModel(id, firstName, lastName, role, photo));
                    }
                    usersLiveData.setValue(users);
                } else {
                    errorLiveData.setValue(e.getMessage());
                }
            }
        });
    }

    public void changeUserRole(String userId, String newRole) {
        ParseManager.changeUserRole(userId, newRole, new FunctionCallback<Map<String, Object>>() {
            @Override
            public void done(Map<String, Object> result, ParseException e) {
                if (e == null) {
                    // Обновляем роль в LiveData
                    List<UserModel> currentUsers = usersLiveData.getValue();
                    if (currentUsers != null) {
                        for (UserModel user : currentUsers) {
                            if (user.getId().equals(userId)) {
                                user.setRole(newRole);
                                break;
                            }
                        }
                        usersLiveData.setValue(currentUsers);
                    }
                } else {
                    errorLiveData.setValue(e.getMessage());
                }
            }
        });
    }

    public void openChatWithUser(String targetUserId) {
        ParseManager.getOrCreateChat(targetUserId, new FunctionCallback<Map<String, Object>>() {
            @Override
            public void done(Map<String, Object> result, ParseException e) {
                if (e == null) {
                    String chatId = (String) result.get("chatId");
                } else {
                    errorLiveData.setValue(e.getMessage());
                }
            }
        });
    }
}