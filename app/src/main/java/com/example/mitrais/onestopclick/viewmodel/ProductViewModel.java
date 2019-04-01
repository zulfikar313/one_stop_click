package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.App;
import com.example.mitrais.onestopclick.dagger.component.DaggerProductViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;

import java.util.List;

import javax.inject.Inject;

public class ProductViewModel extends AndroidViewModel {
    @Inject
    ProductRepository productRepository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    // return products live data
    public LiveData<List<Product>> getAllProducts() {
        return productRepository.getAllLocalProducts();
    }

    // initialize dagger injection
    private void initDagger(Application application) {
        ProductViewModelComponent component = DaggerProductViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
