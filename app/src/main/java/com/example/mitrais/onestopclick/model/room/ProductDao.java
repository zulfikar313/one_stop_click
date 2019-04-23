package com.example.mitrais.onestopclick.model.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.mitrais.onestopclick.model.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("SELECT * FROM product ORDER BY title")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM product WHERE title LIKE :search OR author LIKE :search OR artist LIKE :search or director LIKE :search ORDER BY title")
    LiveData<List<Product>> searchProducts(String search);

    @Query("SELECT * FROM product WHERE type = :type AND (title LIKE :search OR author LIKE :search OR artist LIKE :search or director LIKE :search) ORDER BY title")
    LiveData<List<Product>> searchProductsByType(String type, String search);

    @Query("SELECT * FROM product WHERE type = :type ORDER BY title")
    LiveData<List<Product>> getProductsByType(String type);

    @Query("SELECT * FROM product WHERE id = :id")
    LiveData<Product> getProductById(String id);
}
