package com.example.mitrais.onestopclick.model.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.mitrais.onestopclick.model.Ownership;

import java.util.List;

@Dao
public interface OwnershipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ownership ownership);

    @Query("SELECT * FROM ownership")
    LiveData<List<Ownership>> getAll();

    @Query("SELECT * FROM ownership WHERE email = :email AND productId = :productId")
    LiveData<List<Ownership>> getByEmailAndProductId(String email, String productId);
}
