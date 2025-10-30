package com.example.autoschoolbtgp.utils;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseManager {

    /**
     * Загрузка списка пользователей
     */
    public static void getAllUsers(FunctionCallback<List<Object>> callback) {
        ParseCloud.callFunctionInBackground("getAllUsers", new HashMap<>(), callback);
    }

    /**
     * Смена роли пользователя
     */
    public static void changeUserRole(String userId, String newRole, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("newRole", newRole);
        ParseCloud.callFunctionInBackground("changeUserRole", params, callback);
    }

    /**
     * Получение или создание чата с пользователем
     */
    public static void getOrCreateChat(String targetUserId, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("targetUserId", targetUserId);
        ParseCloud.callFunctionInBackground("getOrCreateChat", params, callback);
    }

    /**
     * Загрузка списка чатов текущего пользователя
     */
    public static void getChatsForUser(FunctionCallback<List<Object>> callback) {
        ParseCloud.callFunctionInBackground("getChatsForUser", new HashMap<>(), callback);
    }

    /**
     * Загрузка списка сообщений из чата
     */
    public static void getMessages(String chatId, FunctionCallback<List<Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        ParseCloud.callFunctionInBackground("getMessages", params, callback);
    }

    /**
     * Отправка сообщения в чат
     */
    public static void sendMessage(String chatId, String text, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        params.put("text", text);
        ParseCloud.callFunctionInBackground("sendMessage", params, callback);
    }

    /**
     * Выход пользователя из системы
     */
    public static void logout() {
        ParseUser.logOut();
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