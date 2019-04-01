package com.example.mitrais.onestopclick;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static Preferences instance;
    private static SharedPreferences prefs;

    static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences();
            prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        }

        return instance;
    }

    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return prefs.getString(key, "");
    }

    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }
}

