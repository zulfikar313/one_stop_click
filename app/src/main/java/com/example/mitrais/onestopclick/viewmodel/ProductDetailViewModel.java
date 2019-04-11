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

/**
 * ProductDetailViewModel handle data lifecycle for ProductDetailActivity
 */
public class ProductDetailViewModel extends AndroidViewModel {
    @Inject
    ProductRepository productRepository;

    @Inject
    StorageRepository storageRepository;

    /**
     * ProductDetailViewModel constructor
     *
     * @param application application to inject repository class
     */
    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    /**
     * get product live data by id
     *
     * @param id product id
     * @return product live data
     */
    public LiveData<Product> getProductById(String id) {
        return productRepository.getProductById(id);
    }

    /**
     * upload product image
     *
     * @param uri      product image uri
     * @param filename product image filename
     * @return upload product image task
     */
    public Task<Uri> uploadProductImage(Uri uri, String filename) {
        return storageRepository.uploadProductImage(uri, filename);
    }


    /**
     * set product image
     *
     * @param product product object
     * @return set product image task
     */
    public Task<Void> setProductImage(Product product) {
        return productRepository.setProductImage(product);
    }


    /**
     * set product details
     *
     * @param product product object
     * @return set product details task
     */
    public Task<Void> setProductDetails(Product product) {
        return productRepository.setProductDetails(product);
    }

    /**
     * add product image
     *
     * @param product product object
     * @return add product image task
     */
    public Task<DocumentReference> addProductImage(Product product) {
        return productRepository.addProductImage(product);
    }

    /**
     * add product details
     *
     * @param product product object
     * @return add product details task
     */
    public Task<DocumentReference> addProductDetails(Product product) {
        return productRepository.addProductDetails(product);
    }

    /**
     * initialize dagger injection
     *
     * @param application application to inject repository class
     */
    private void initDagger(Application application) {
        ProductDetailViewModelComponent component = DaggerProductDetailViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
