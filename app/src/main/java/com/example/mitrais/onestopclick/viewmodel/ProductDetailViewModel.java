package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerProductDetailViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductDetailViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import javax.inject.Inject;

public class ProductDetailViewModel extends AndroidViewModel {
    @Inject
    ProductRepository productRepository;

    @Inject
    StorageRepository storageRepository;

    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    // initialize dagger injection
    private void initDagger(Application application) {
        ProductDetailViewModelComponent component = DaggerProductDetailViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }

    public LiveData<Product> getProductById(String id) {
        return productRepository.getProductById(id);
    }

    // save actual image file to storage
    public Task<Uri> saveProductImage(Uri uri, String fileName) {
        return storageRepository.saveProductImage(uri, fileName);
    }

    public Task<Void> saveProductImageData(Product product) {
        return productRepository.setProductImage(product);
    }

    public Task<Void> saveProductDetails(Product product) {
        return productRepository.setProductDetails(product);
    }

    public Task<DocumentReference> addProductImageData(Product product) {
        return productRepository.addProductImage(product);
    }

    public Task<DocumentReference> addProductDetails(Product product) {
        return productRepository.addProductDetails(product);
    }
}
