package com.example.mitrais.onestopclick.model.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.mitrais.onestopclick.model.Profile;

@Dao
public interface ProfileDao {
    @Query("SELECT * FROM profile WHERE email = :email")
    LiveData<Profile> get(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);

    @Query("DELETE FROM profile")
    void delete();
}
