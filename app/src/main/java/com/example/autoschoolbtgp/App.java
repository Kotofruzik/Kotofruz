package com.example.autoschoolbtgp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Инициализация Parse
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Q1tuULttUpvqlPEzH2htnph5sHK6VJvD50rBsywv")
                .clientKey("YOzg5tkHlzW08CBXggeOze8iG6Tx5LWx7gD83S33")
                .server("https://parseapi.back4app.com/")
                .build()
        );

        // Канал уведомлений для Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default",
                    "Push Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Уведомления от автошколы");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        // Устанавливаем только GCMSenderId — deviceToken будет установлен автоматически
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "783885501862");
        installation.saveInBackground();

        ParsePush.subscribeInBackground("", e -> {
           if (e != null){
               Log.e("PARSE", "Parse subscribe failed", e);
           } else {
               Log.i("PARSE", "Push subscribed - deviceToken now be saved");
           }
        });
    }
}