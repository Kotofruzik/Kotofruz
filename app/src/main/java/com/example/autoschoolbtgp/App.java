package com.example.autoschoolbtgp;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Инициализация Parse SDK с вашими ключами и адресом сервера
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Q1tuULttUpvqlPEzH2htnph5sHK6VJvD50rBsywv")  // замените на ваш applicationId
                .clientKey("YOzg5tkHlzW08CBXggeOze8iG6Tx5LWx7gD83S33")          // замените на ваш clientKey
                .server("https://parseapi.back4app.com/") // URL сервера Parse
                .build()
        );
    }
}
