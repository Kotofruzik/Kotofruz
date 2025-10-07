package com.example.autoschoolbtgp.ui.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.autoschoolbtgp.ui.utils.ParseManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<List<UserModel>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LiveData<List<UserModel>> getUsers() {
        return usersLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void loadUsers() {
        ParseManager.getAllUsers(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    List<UserModel> users = new ArrayList<>();
                    for (ParseUser user : parseUsers) {
                        String id = user.getObjectId();
                        String firstName = user.getString("firstName");
                        String lastName = user.getString("lastName");
                        String role = user.getString("role");
                        String photoUrl = user.getParseFile("photo") != null ? user.getParseFile("photo").getUrl() : null;

                        users.add(new UserModel(id, firstName, lastName, role, photoUrl));
                    }
                    usersLiveData.setValue(users);
                } else {
                    errorLiveData.setValue(e.getMessage());
                }
            }
        });
    }

    public void changeUserRole(String userId, String newRole) {
        ParseManager.changeUserRole(userId, newRole, e -> {
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
        });
    }

    public void openChatWithUser(String targetUserId) {
        // Если Cloud Function для чата всё ещё нужна — можно оставить
        // или реализовать через ParseObject "Chat"
    }
}