package com.example.mitrais.onestopclick.model.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.mitrais.onestopclick.model.Comment;

import java.util.List;

@Dao
public interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comment comment);

    @Delete
    void delete(Comment comment);

    @Query("DELETE FROM comment WHERE productId = :productId")
    void deleteByProductId(String productId);

    @Query("SELECT * FROM comment")
    LiveData<List<Comment>> getAll();

    @Query("SELECT * FROM comment WHERE productId = :productId")
    LiveData<List<Comment>> getByProductId(String productId);
}