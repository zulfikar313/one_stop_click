package com.example.mitrais.onestopclick;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public static final String SHARED_PREFS = "sharedPrefs";
    private static Preferences instance;
    private static SharedPreferences prefs;

    static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences();
            prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        }

        return instance;
    }

    // Put string value to shared prefs
    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    // Get string value from shared prefs
    public String getString(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    // Put boolean value to shared prefs
    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    //  Get boolean value from shared prefs
    public boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }
}

