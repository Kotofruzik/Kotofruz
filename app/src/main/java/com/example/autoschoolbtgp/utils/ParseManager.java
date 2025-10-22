package com.example.autoschoolbtgp.utils;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseManager {

    /**
     * Загрузка списка пользователей через Cloud Function
     */
    public static void getAllUsers(FunctionCallback<List<Object>> callback) {
        ParseCloud.callFunctionInBackground("getAllUsers", new HashMap<>(), callback);
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

    public static void getOrCreateChat(String targetUserId, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("targetUserId", targetUserId);
        ParseCloud.callFunctionInBackground("getOrCreateChat", params,callback);
    }

    public static void sendMessage(String chatId, String text, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        params.put("text", text);
        ParseCloud.callFunctionInBackground("sendMessage", params, callback);
    }

    public static void getMessages(String chatId, FunctionCallback<Map<String, Object>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        ParseCloud.callFunctionInBackground("getMessages", params, callback);
    }
}