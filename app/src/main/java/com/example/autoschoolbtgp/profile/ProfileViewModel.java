package com.example.autoschoolbtgp.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autoschoolbtgp.adminPanel.users.UserModel;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<UserModel> userData = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPhotoUploading = new MutableLiveData<>(false);

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

    public LiveData<Boolean> getIsPhotoUploading() {
        return isPhotoUploading;
    }

    public void loadCurrentUser() {
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
            }

            UserModel model = new UserModel(id, firstName, lastName, role, photoUrl);
            model.setMiddleName(middleName);
            userData.setValue(model);
        } else {
            error.setValue("Пользователь не авторизован");
        }
    }

    public void setNewPhotoBytes(byte[] bytes) {
        this.newPhotoBytes = bytes;
    }

    public void uploadNewPhotoAndSaveProfile(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            error.setValue("Невозможно загрузить пустое фото");
            isPhotoUploading.setValue(false);
            return;
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            error.setValue("Пользователь не авторизован");
            isPhotoUploading.setValue(false);
            return;
        }

        isPhotoUploading.setValue(true);

        ParseFile photoFile = new ParseFile("profile_photo.jpg", imageBytes);
        photoFile.saveInBackground((com.parse.SaveCallback) e -> {
            if (e == null) {
                saveUserWithNewPhoto(currentUser, photoFile);
            } else {
                isPhotoUploading.setValue(false);
                error.setValue("Ошибка загрузки фото: " + e.getMessage());
            }
        });
    }

    private void saveUserWithNewPhoto(ParseUser user, ParseFile newPhotoFile) {
        user.put("photo", newPhotoFile);

        user.saveInBackground((SaveCallback) e -> {
            isPhotoUploading.setValue(false);
            if (e == null) {
                successMessage.setValue("Фото профиля успешно обновлено!");

                String id = user.getObjectId();
                String firstName = user.getString("firstName");
                String lastName = user.getString("lastName");
                String middleName = user.getString("middleName");
                String role = user.getString("role");
                String photoUrl = newPhotoFile.getUrl();

                UserModel updatedModel = new UserModel(id, firstName, lastName, role, photoUrl);
                updatedModel.setMiddleName(middleName);
                userData.setValue(updatedModel);

            } else {
                error.setValue("Ошибка сохранения профиля: " + e.getMessage());
            }
        });
    }

    public void updateProfileTextOnly(String firstName, String lastName, String middleName, String newPassword) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            error.setValue("Пользователь не авторизован");
            return;
        }

        currentUser.put("firstName", firstName);
        currentUser.put("lastName", lastName);
        currentUser.put("middleName", middleName);

        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        currentUser.saveInBackground((SaveCallback) e -> {
            if (e == null) {
                successMessage.setValue("Текстовые данные профиля обновлены.");

                String id = currentUser.getObjectId();
                String currentPhotoUrl = (userData.getValue() != null) ? userData.getValue().getAvatarUrl() : null;

                UserModel updatedModel = new UserModel(id, firstName, lastName, currentUser.getString("role"), currentPhotoUrl);
                updatedModel.setMiddleName(middleName);
                userData.setValue(updatedModel);

            } else {
                error.setValue("Ошибка сохранения данных: " + e.getMessage());
            }
        });
    }

    public void logout() {
        ParseUser.logOut();
    }
}