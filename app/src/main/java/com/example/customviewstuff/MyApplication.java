package com.example.customviewstuff;

import android.app.Application;

public class MyApplication extends Application {
    public static Application app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
