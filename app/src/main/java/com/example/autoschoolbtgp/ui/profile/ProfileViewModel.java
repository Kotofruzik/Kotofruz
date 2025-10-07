package com.example.autoschoolbtgp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autoschoolbtgp.ui.utils.ParseManager;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<UserData> userData = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();

    private byte[] newPhotoBytes = null;

    public LiveData<UserData> getUserData() {
        return userData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadCurrentUser() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            UserData data = new UserData(
                    user.getString("firstName"),
                    user.getString("lastName"),
                    user.getString("middleName")
            );
            userData.setValue(data);
        } else {
            error.setValue("Пользователь не авторизован");
        }
    }

    public void setNewPhotoBytes(byte[] bytes) {
        this.newPhotoBytes = bytes;
    }

    public void updateProfile(String firstName, String lastName, String middleName, String newPassword) {
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            error.setValue("Пользователь не авторизован");
            return;
        }

        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("middleName", middleName);

        // Если введён новый пароль — обновляем
        if (!newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }

        // Если выбрано новое фото — загружаем его
        if (newPhotoBytes != null) {
            ParseFile photoFile = new ParseFile("profile_photo.jpg", newPhotoBytes);
            photoFile.saveInBackground((com.parse.SaveCallback) e -> {
                if (e == null) {
                    user.put("photo", photoFile);
                    saveUser(user);
                } else {
                    error.setValue("Ошибка загрузки фото: " + e.getMessage());
                }
            });
        } else {
            // Если фото не менялось — просто сохраняем остальные данные
            saveUser(user);
        }
    }

    private void saveUser(ParseUser user) {
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    successMessage.setValue("Профиль обновлён");
                    // Обновим LiveData
                    UserData data = new UserData(
                            user.getString("firstName"),
                            user.getString("lastName"),
                            user.getString("middleName")
                    );
                    userData.setValue(data);
                } else {
                    error.setValue("Ошибка сохранения: " + e.getMessage());
                }
            }
        });
    }

    public void logout() {
        ParseUser.logOut();
    }

    // Вспомогательный класс для данных
    public static class UserData {
        public final String firstName;
        public final String lastName;
        public final String middleName;

        public UserData(String firstName, String lastName, String middleName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
        }
    }
}