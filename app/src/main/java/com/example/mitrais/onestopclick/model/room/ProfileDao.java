package com.example.mitrais.onestopclick.model.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.mitrais.onestopclick.model.Profile;

import java.util.List;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);

    @Query("SELECT * FROM profile")
    LiveData<List<Profile>> getAll();

    @Query("SELECT * FROM profile WHERE email = :email")
    LiveData<Profile> get(String email);

    @Query("DELETE FROM profile")
    void delete();
}
