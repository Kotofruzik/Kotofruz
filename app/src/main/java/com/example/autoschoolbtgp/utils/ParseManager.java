package com.example.autoschoolbtgp.utils;

import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseManager {
    public static void getAllUsers(FunctionCallback<List<Object>> callback) {
        ParseCloud.callFunctionInBackground("getAllUsers", new HashMap<>(), callback);
    }

    public static void changeUserRole(String userId, String newRole, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("newRole", newRole);
        ParseCloud.callFunctionInBackground("changeUserRole", params, callback);
    }

    public static void getOrCreateChat(String targetUserId, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("targetUserId", targetUserId);
        ParseCloud.callFunctionInBackground("getOrCreateChat", params, callback);
    }

    public static void getMessages(String chatId, FunctionCallback<List<Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        ParseCloud.callFunctionInBackground("getMessages", params, callback);
    }

    public static void sendMessage(String chatId, String text, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        params.put("text", text);
        ParseCloud.callFunctionInBackground("sendMessage", params, callback);
    }

    public static void getChatsForUser(FunctionCallback<List<Object>> callback) {
        Log.d("ParseManager", "вызов getChatsForUser");
        ParseCloud.callFunctionInBackground("getChatsForUser", new HashMap<>(), callback);
    }

    public static void logSuccess(String message) {
        android.util.Log.d("ParseManager", message);
    }

    public static void logError(ParseException e) {
        android.util.Log.e("ParseManager", "Ошибка Parse: " + e.getMessage());
    }
}