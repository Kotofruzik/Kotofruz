package com.example.autoschoolbtgp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    private static final String TAG = "NotificationWorker";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d(TAG, "🎯 NotificationWorker создан");
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "🚀 =========== doWork ЗАПУЩЕН ===========");

        try {
            String message = getInputData().getString("message");
            Log.d(TAG, "📨 Получено сообщение: " + message);

            if (message == null || message.isEmpty()) {
                Log.e(TAG, "❌ Сообщение пустое!");
                return Result.failure();
            }

            Log.d(TAG, "🔄 Показываем уведомление...");
            showNotification("Автошкола", message);
            Log.d(TAG, "✅ Уведомление успешно показано!");

            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "❌ ОШИБКА в doWork: ", e);
            return Result.failure();
        }
    }

    private void showNotification(String title, String message) {
        Log.d(TAG, "🔔 showNotification: " + title + " - " + message);

        try {
            Context context = getApplicationContext();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager == null) {
                Log.e(TAG, "❌ NotificationManager is NULL!");
                return;
            }

            String channelId = "reminders_channel";
            Log.d(TAG, "🔧 Создаем канал: " + channelId);

            // Создаем канал для Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Напоминания",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Канал для напоминаний автошколы");
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "✅ Канал создан");
            } else {
                Log.d(TAG, "📱 Android < 8.0, канал не нужен");
            }

            // Создаем уведомление
            Log.d(TAG, "🔨 Создаем билдер уведомления");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_info) // Системная иконка для теста
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            int notificationId = (int) System.currentTimeMillis();
            Log.d(TAG, "📤 Показываем уведомление ID: " + notificationId);
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "🎉 Уведомление отправлено в систему!");

        } catch (Exception e) {
            Log.e(TAG, "❌ ОШИБКА при создании уведомления: ", e);
        }
    }
}