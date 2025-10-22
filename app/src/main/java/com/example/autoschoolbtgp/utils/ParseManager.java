package com.example.autoschoolbtgp.utils;

import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseManager {

    /**
     * Загрузка списка пользователей через Cloud Function
     */
    // В ParseManager.java

    public static void getAllUsers(FunctionCallback<List<Object>> callback) {
        Log.d("ParseManager", "Вызов getAllUsers");
        ParseCloud.callFunctionInBackground("getAllUsers", new HashMap<>(), new FunctionCallback<List<Object>>() {
            @Override
            public void done(List<Object> result, ParseException e) {
                // --- ИСПРАВЛЕНИЕ: Проверка на null ---
                if (e == null && result != null) {
                    callback.done(result, null);
                } else if (e != null) {
                    callback.done(null, e);
                } else {
                    // result == null, e == null
                    callback.done(new ArrayList<>(), null); // Передаем пустой список
                }
                // --- ИСПРАВЛЕНИЕ КОНЕЦ ---
            }
        });
    }

    /**
     * Смена роли пользователя через Cloud Function
     */
    public static void changeUserRole(String userId, String newRole, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("newRole", newRole);
        ParseCloud.callFunctionInBackground("changeUserRole", params, callback);
    }

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

    public static void getOrCreateChat(String targetUserId, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("targetUserId", targetUserId);
        ParseCloud.callFunctionInBackground("getOrCreateChat", params, callback);
    }
}