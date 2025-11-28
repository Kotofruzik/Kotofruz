package com.example.autoschoolbtgp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Q1tuULttUpvqlPEzH2htnph5sHK6VJvD50rBsywv")
                .clientKey("YOzg5tkHlzW08CBXggeOze8iG6Tx5LWx7gD83S33")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
