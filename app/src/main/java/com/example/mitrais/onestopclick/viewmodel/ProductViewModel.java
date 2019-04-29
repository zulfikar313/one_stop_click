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

public class ProductViewModel extends AndroidViewModel {
    @Inject
    ProductRepository productRepository;

    @Inject
    StorageRepository storageRepository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    /**
     * @param id product id
     * @return product live data
     */
    public LiveData<Product> getProductById(String id) {
        return productRepository.getProductById(id);
    }

    /**
     * @param uri      product image uri
     * @param filename product image filename
     * @return upload product image task
     */
    public Task<Uri> uploadProductImage(Uri uri, String filename) {
        return storageRepository.uploadProductImage(uri, filename);
    }

    /**
     * @param uri      trailer uri
     * @param filename trailer filename
     * @return upload trailer task
     */
    public Task<Uri> uploadTrailer(Uri uri, String filename) {
        return storageRepository.uploadTrailer(uri, filename);
    }

    /**
     * set product
     *
     * @param product product object
     * @return save product task
     */
    public Task<Void> saveProduct(Product product) {
        return productRepository.saveProduct(product);
    }


    /**
     * @param productId   product id
     * @param trailer1Uri trailer1 uri
     * @return save product task
     */
    public Task<Void> saveProductTrailer(String productId, Uri trailer1Uri) {
        return productRepository.saveProductTrailerUri(productId, trailer1Uri);
    }

    /**
     * add product
     *
     * @param product product object
     * @return add product task
     */
    public Task<DocumentReference> addProduct(Product product) {
        return productRepository.addProduct(product);
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
