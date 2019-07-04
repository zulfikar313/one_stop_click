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

    @Query("SELECT comment.productId, comment.email, profile.username, profile.imageUri as userImageUri, ownership.rating as userRate, comment.date, comment.content " +
            "FROM comment " +
            "INNER JOIN profile ON comment.email = profile.email " +
            "INNER JOIN ownership ON comment.productId == ownership.productId AND comment.email == ownership.email AND profile.email == ownership.email " +
            "ORDER BY date")
    LiveData<List<Comment>> getAll();

    @Query("SELECT comment.productId, comment.email, comment.date, profile.username as username, profile.imageUri as userImageUri, ownership.rating as userRate, comment.content " +
            "FROM comment " +
            "INNER JOIN ownership ON comment.productId == ownership.productId AND comment.email == ownership.email " +
            "INNER JOIN profile ON comment.email = profile.email " +
            "WHERE comment.productId = :productId " +
            "ORDER BY date")
    LiveData<List<Comment>> getByProductId(String productId);
}