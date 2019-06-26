package com.example.mitrais.onestopclick.model.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.Profile;

@Database(entities = {Profile.class, Product.class}, version = 25)
@TypeConverters(Converter.class)
public abstract class MainDatabase extends RoomDatabase {
    private static MainDatabase instance;

    public abstract ProfileDao profileDao();

    public abstract ProductDao productDao();

    public static synchronized MainDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, MainDatabase.class, "main_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
