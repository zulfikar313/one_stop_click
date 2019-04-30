package com.example.mitrais.onestopclick.model.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.mitrais.onestopclick.model.Profile;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProfile(Profile profile);

    @Update
    void updateProfile(Profile profile);

    @Query("DELETE FROM profile")
    void deleteProfile();

    @Query("DELETE FROM profile WHERE email = :email")
    void deleteProfileByEmail(String email);

    @Query("SELECT * FROM profile WHERE email = :email")
    LiveData<Profile> getProfile(String email);
}
