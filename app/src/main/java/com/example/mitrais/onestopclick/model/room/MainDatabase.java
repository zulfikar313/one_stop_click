package com.example.mitrais.onestopclick.model.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.mitrais.onestopclick.model.Comment;
import com.example.mitrais.onestopclick.model.Ownership;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.Profile;

@Database(entities = {Profile.class, Ownership.class, Product.class, Comment.class}, version = 35)
@TypeConverters(Converter.class)
public abstract class MainDatabase extends RoomDatabase {
    private static MainDatabase instance;

    public abstract ProfileDao profileDao();

    public abstract OwnershipDao ownershipDao();

    public abstract ProductDao productDao();

    public abstract CommentDao commentDao();

    public static synchronized MainDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, MainDatabase.class, "main_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
