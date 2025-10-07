package com.example.autoschoolbtgp.ui.utils;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.List;

public class ParseManager {

    /**
     * Загрузка списка пользователей
     */
    public static void getAllUsers(FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.findInBackground(callback);
    }

    /**
     * Смена роли пользователя
     */
    public static void changeUserRole(String userId, String newRole, SaveCallback callback) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.getInBackground(userId, (user, e) -> {
            if (e == null && user != null) {
                user.put("role", newRole);
                user.saveInBackground(callback);
            } else {
                android.util.Log.e("ParseManager", "Ошибка при получении пользователя: " + e.getMessage());
            }
        });
    }

    /**
     * Логирование
     */
    public static void logSuccess(String message) {
        android.util.Log.d("ParseManager", message);
    }

    public static void logError(ParseException e) {
        android.util.Log.e("ParseManager", "Ошибка Parse: " + e.getMessage());
    }
}