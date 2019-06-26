package com.example.mitrais.onestopclick.model.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.mitrais.onestopclick.model.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM product ORDER BY title")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM product WHERE id = :id")
    LiveData<Product> getById(String id);

    @Query("SELECT * FROM product WHERE type = :type ORDER BY title")
    LiveData<List<Product>> getByType(String type);

    @Query("SELECT * FROM product WHERE genre = :genre ORDER BY title")
    LiveData<List<Product>> getByGenre(String genre);

    @Query("SELECT * FROM product WHERE type = :type AND genre = :genre ORDER BY title")
    LiveData<List<Product>> getByTypeAndGenre(String type, String genre);

    @Query("SELECT * FROM product WHERE title LIKE :search OR author LIKE :search OR artist LIKE :search or director LIKE :search ORDER BY title")
    LiveData<List<Product>> search(String search);

    @Query("SELECT * FROM product WHERE isInCart = 1 AND isOwned = 0")
    LiveData<List<Product>> getProductsInCart();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Delete
    void delete(Product product);

    @Query("UPDATE product SET isLiked = 0, isDisliked = 0")
    void deleteBoundData();
}
