package com.example.mitrais.onestopclick;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * App class to access singleton classess
 */
public class App extends Application {
    public static Preferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        prefs = Preferences.getInstance(this);
    }
}


