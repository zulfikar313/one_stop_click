package com.example.mitrais.onestopclick.view.main.product_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
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

    public LiveData<List<Product>> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public LiveData<List<Product>> getProductsByType(String type) {
        return productRepository.getProductsByType(type);
    }

    public Task<Void> addLike(String id) {
        return productRepository.addLike(id);
    }

    public Task<Void> addDislike(String id) {
        return productRepository.addDislike(id);
    }

    /**
     * initialize dagger injection
     *
     * @param application for repository injection
     */
    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
