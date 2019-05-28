package com.example.mitrais.onestopclick.model.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.mitrais.onestopclick.model.ProfileProduct;

import java.util.List;

@Dao
public interface ProfileProductDao {
    @Query("SELECT * FROM profile_product")
    LiveData<List<ProfileProduct>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProfileProduct profileProduct);

    @Delete
    void delete(ProfileProduct profileProduct);

    @Query("DELETE from profile_product")
    void deleteAll();
}
