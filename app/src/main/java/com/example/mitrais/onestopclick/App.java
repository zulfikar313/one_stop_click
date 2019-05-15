package com.example.mitrais.onestopclick;

import android.app.Application;
import android.app.Service;
import android.net.ConnectivityManager;

public class App extends Application {
    public static Preferences prefs;
    public static ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        prefs = Preferences.getInstance(this);
        connectivityManager = (ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
    }

    /**
     * @return true if app online
     */
    public static boolean isOnline() {
        return connectivityManager.getActiveNetwork() != null;
    }
}


