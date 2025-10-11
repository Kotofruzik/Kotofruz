package com.example.autoschoolbtgp.ui.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autoschoolbtgp.ui.users.UserModel;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = "ProfileViewModel_SENIOR";

    private MutableLiveData<UserModel> userData = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();

    private byte[] newPhotoBytes = null;

    public LiveData<UserModel> getUserData() {
        return userData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadCurrentUser() {
        Log.d(TAG, "loadCurrentUser: Начало загрузки данных текущего пользователя из Parse.");
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            String id = currentUser.getObjectId();
            String firstName = currentUser.getString("firstName");
            String lastName = currentUser.getString("lastName");
            String middleName = currentUser.getString("middleName");
            String role = currentUser.getString("role");

            String photoUrl = null;
            ParseFile photoFile = currentUser.getParseFile("photo");
            if (photoFile != null) {
                photoUrl = photoFile.getUrl();
                Log.d(TAG, "loadCurrentUser: URL фото пользователя из ParseFile: " + photoUrl);
            } else {
                Log.d(TAG, "loadCurrentUser: У пользователя нет фото (ParseFile 'photo' отсутствует или null).");
            }

            UserModel model = new UserModel(id, firstName, lastName, role, photoUrl);
            model.setMiddleName(middleName);
            userData.setValue(model);
            Log.d(TAG, "loadCurrentUser: Данные пользователя загружены и переданы в LiveData.");
        } else {
            String errorMsg = "Пользователь не авторизован";
            Log.w(TAG, "loadCurrentUser: " + errorMsg);
            error.setValue(errorMsg);
        }
    }

    public void setNewPhotoBytes(byte[] bytes) {
        this.newPhotoBytes = bytes;
    }

    public void updateProfile(String firstName, String lastName, String middleName, String newPassword) {
        Log.d(TAG, "updateProfile: Начало обновления профиля. Имя: " + firstName + ", Фамилия: " + lastName + ", Отчество: " + middleName + ", Пароль меняется: " + (!newPassword.isEmpty()));
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            String errorMsg = "Пользователь не авторизован";
            Log.e(TAG, "updateProfile: " + errorMsg);
            error.setValue(errorMsg);
            return;
        }

        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("middleName", middleName);

        if (!newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }

        if (newPhotoBytes != null && newPhotoBytes.length > 0) {
            Log.d(TAG, "updateProfile: Новое фото выбрано. Начинаем загрузку.");
            ParseFile photoFile = new ParseFile("profile_photo.jpg", newPhotoBytes);
            photoFile.saveInBackground((com.parse.SaveCallback)e -> {
                if (e == null) {
                    Log.d(TAG, "updateProfile: Файл фото загружен. URL: " + photoFile.getUrl());
                    user.put("photo", photoFile);
                    saveUser(user);
                } else {
                    String errorMsg = "Ошибка загрузки фото: " + e.getMessage();
                    Log.e(TAG, "updateProfile: " + errorMsg, e);
                    error.setValue(errorMsg);
                }
            });
        } else {
            Log.d(TAG, "updateProfile: Новое фото не выбрано или байты пусты. Сохраняем только текстовые данные.");
            saveUser(user);
        }
    }

    private void saveUser(ParseUser user) {
        user.saveInBackground(e -> {
            if (e == null) {
                Log.d(TAG, "saveUser: Пользователь успешно сохранён.");
                successMessage.setValue("Профиль обновлён");

                String id = user.getObjectId();
                String firstName = user.getString("firstName");
                String lastName = user.getString("lastName");
                String middleName = user.getString("middleName");
                String role = user.getString("role");

                String photoUrl = null;
                ParseFile photoFile = user.getParseFile("photo");
                if (photoFile != null) {
                    photoUrl = photoFile.getUrl();
                }

                UserModel updatedModel = new UserModel(id, firstName, lastName, role, photoUrl);
                updatedModel.setMiddleName(middleName);
                userData.setValue(updatedModel);

                newPhotoBytes = null;
            } else {
                String errorMsg = "Ошибка сохранения: " + e.getMessage();
                Log.e(TAG, "saveUser: " + errorMsg, e);
                error.setValue(errorMsg);
            }
        });
    }

    public void updateProfileTextOnly(String firstName, String lastName, String middleName, String newPassword) {
        Log.d(TAG, "updateProfileTextOnly: Начало обновления текстовых данных профиля.");
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            String errorMsg = "Пользователь не авторизован";
            Log.e(TAG, "updateProfileTextOnly: " + errorMsg);
            error.setValue(errorMsg);
            return;
        }

        currentUser.put("firstName", firstName);
        currentUser.put("lastName", lastName);
        currentUser.put("middleName", middleName);

        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        currentUser.saveInBackground(e -> {
            if (e == null) {
                Log.d(TAG, "updateProfileTextOnly: Текстовые данные успешно сохранены.");
                successMessage.setValue("Текстовые данные профиля обновлены.");

                String id = currentUser.getObjectId();
                String role = currentUser.getString("role");

                String photoUrl = null;
                ParseFile photoFile = currentUser.getParseFile("photo");
                if (photoFile != null) {
                    photoUrl = photoFile.getUrl();
                }

                UserModel updatedModel = new UserModel(id, firstName, lastName, role, photoUrl);
                updatedModel.setMiddleName(middleName);
                userData.setValue(updatedModel);

            } else {
                String errorMsg = "Ошибка сохранения текстовых данных: " + e.getMessage();
                Log.e(TAG, "updateProfileTextOnly: " + errorMsg, e);
                error.setValue(errorMsg);
            }
        });
    }

    public void logout() {
        Log.d(TAG, "logout: Выход пользователя из системы.");
        ParseUser.logOut();
    }
}