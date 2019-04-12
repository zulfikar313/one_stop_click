package com.example.mitrais.onestopclick;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preferences class provide access to shared preferences
 */
public class Preferences {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static Preferences instance;
    private static SharedPreferences prefs;

    /**
     * Preferences constructor
     *
     * @param context context to create shared preference instance
     * @return Preferences singleton instance
     */
    static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences();
            prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        }

        return instance;
    }

    /**
     * put String preference
     *
     * @param key   preference key
     * @param value preference value
     */
    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    /**
     * get String preference
     *
     * @param key preference key
     * @return String preference
     */
    public String getString(String key) {
        return prefs.getString(key, "");
    }

    /**
     * put boolean preference
     *
     * @param key   preference key
     * @param value preferenvce value
     */
    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    /**
     * get boolean preference
     *
     * @param key preference key
     * @return boolean preference
     */
    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }
}

