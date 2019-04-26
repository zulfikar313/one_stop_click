package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerProductViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;

public class ProductListViewModel extends AndroidViewModel {
    @Inject
    ProductRepository productRepository;

    public ProductListViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    /**
     * @return all products
     */
    public LiveData<List<Product>> getAllProducts() {
        return productRepository.getAllProducts();
    }

    /**
     * @return product list live data
     */
    public LiveData<List<Product>> getProductsByType(String type) {
        return productRepository.getProductsByType(type);
    }

    /**
     * @param search title, author, artist or director
     * @return product search results live data
     */
    public LiveData<List<Product>> searchProducts(String search) {
        return productRepository.searchProducts(search);
    }

    /**
     * @param type   product type
     * @param search title, author, artist or director
     * @return product search results live data
     */
    public LiveData<List<Product>> searchProductsByType(String type, String search) {
        return productRepository.searchProductsByType(type, search);
    }

    /**
     * increase like count
     *
     * @param id product id
     * @return add like task
     */
    public Task<Void> addLike(String id) {
        return productRepository.addLike(id);
    }

    /**
     * increase dislike count
     *
     * @param id product id
     * @return add dislike task
     */
    public Task<Void> addDislike(String id) {
        return productRepository.addDislike(id);
    }

    /**
     * initialize dagger injection
     *
     * @param application application to inject repository class
     */
    private void initDagger(Application application) {
        ProductViewModelComponent component = DaggerProductViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
