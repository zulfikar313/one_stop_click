package com.example.mitrais.onestopclick;

import android.app.Application;

public class App extends Application {
    public static Preferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = Preferences.getInstance(this);
    }
}


