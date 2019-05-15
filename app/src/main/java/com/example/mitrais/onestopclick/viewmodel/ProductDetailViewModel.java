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
import com.google.firebase.storage.FileDownloadTask;

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

    /**
     * @param id product id
     * @return product live data
     */
    public LiveData<Product> getProductById(String id) {
        return productRepository.getProductById(id);
    }

    /**
     * @param uri      book uri
     * @param filename book filename
     * @return task
     */
    public Task<Uri> uploadBook(Uri uri, String filename) {
        return storageRepository.uploadBook(uri, filename);
    }

    /**
     * @param localUri book local uri
     * @param filename book filename
     * @return task
     */
    public FileDownloadTask downloadBook(Uri localUri, String filename) {
        return storageRepository.downloadBook(localUri, filename);
    }

    /**
     * @param uri      music uri
     * @param filename music filename
     * @return task
     */
    public Task<Uri> uploadMusic(Uri uri, String filename) {
        return storageRepository.uploadMusic(uri, filename);
    }

    /**
     * @param uri      thumbnail uri
     * @param filename thumbnail filename
     * @return task
     */
    public Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageRepository.uploadThumbnail(uri, filename);
    }

    /**
     * @param uri      trailer uri
     * @param filename trailer filename
     * @return task
     */
    public Task<Uri> uploadTrailer(Uri uri, String filename) {
        return storageRepository.uploadTrailer(uri, filename);
    }

    /**
     * @param product product object
     * @return task
     */
    public Task<Void> saveProduct(Product product) {
        return productRepository.saveProduct(product);
    }

    /**
     * @param product product object
     * @return task
     */
    public Task<DocumentReference> addProduct(Product product) {
        return productRepository.addProduct(product);
    }

    /**
     * @param id  product id
     * @param uri thumbnail uri
     * @return save product task
     */
    public Task<Void> saveThumbnailUri(String id, Uri uri) {
        return productRepository.saveThumbnailUri(id, uri);
    }

    /**
     * @param id  product id
     * @param uri book uri
     * @return task
     */
    public Task<Void> saveProductBook(String id, Uri uri) {
        return productRepository.saveProductBookUri(id, uri);
    }

    /**
     * @param id  product id
     * @param uri music uri
     * @return task
     */
    public Task<Void> saveProductMusic(String id, Uri uri) {
        return productRepository.saveProductMusicUri(id, uri);
    }

    /**
     * @param id  product id
     * @param uri trailer uri
     * @return task
     */
    public Task<Void> saveProductTrailer(String id, Uri uri) {
        return productRepository.saveProductTrailerUri(id, uri);
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
