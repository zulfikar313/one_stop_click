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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM product WHERE id = :id")
    LiveData<Product> getProductById(String id);

    @Query("SELECT * FROM product WHERE type = :type ORDER BY title")
    LiveData<List<Product>> getProductByType(String type);

    @Query("SELECT * FROM product WHERE genre = :genre ORDER BY title")
    LiveData<List<Product>> getProductByGenre(String genre);

    @Query("SELECT * FROM product WHERE type = :productType AND genre = :genre ORDER BY title")
    LiveData<List<Product>> getProducyByTypeAndGenre(String productType, String genre);

    @Query("SELECT * FROM product WHERE title LIKE :query OR author LIKE :query OR artist LIKE :query or director LIKE :query ORDER BY title")
    LiveData<List<Product>> searchProductByQuery(String query);

    @Query("SELECT * FROM product WHERE isInCart = 1 AND isOwned = 0")
    LiveData<List<Product>> getInCart();

    @Query("SELECT * FROM product WHERE isOwned = 1")
    LiveData<List<Product>> getOwned();
}
