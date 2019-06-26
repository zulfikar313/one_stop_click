package com.example.mitrais.onestopclick.model.room;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

class Converter {
    @TypeConverter
    static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    static HashMap<String, Float> hashMapFromString(String value) {
        Type listType = new TypeToken<HashMap<String, Float>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    static String fromHashMap(HashMap<String, Float> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}