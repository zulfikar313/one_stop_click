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
    public Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageRepository.uploadThumbnail(uri, filename);
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
     * @param uri      music uri
     * @param filename music filename
     * @return upload music task
     */
    public Task<Uri> uploadMusic(Uri uri, String filename) {
        return storageRepository.uploadMusic(uri, filename);
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
     * add product
     *
     * @param product product object
     * @return save product task
     */
    public Task<DocumentReference> addProduct(Product product) {
        return productRepository.addProduct(product);
    }


    /**
     * @param productId product id
     * @param uri       thumbnail uri
     * @return save product task
     */
    public Task<Void> saveThumbnailUri(String productId, Uri uri) {
        return productRepository.saveThumbnailUri(productId, uri);
    }

    /**
     * @param productId product id
     * @param uri       trailer uri
     * @return save product task
     */
    public Task<Void> saveProductTrailer(String productId, Uri uri) {
        return productRepository.saveProductTrailerUri(productId, uri);
    }

    /**
     * @param productId product id
     * @param uri       music uri
     * @return save product task
     */
    public Task<Void> saveProductMusic(String productId, Uri uri) {
        return productRepository.saveProductMusicUri(productId, uri);
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
