package com.example.mitrais.onestopclick.view.edit_book;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import javax.inject.Inject;

public class EditBookViewModel extends AndroidViewModel {
    @Inject
    StorageRepository storageRepository;

    @Inject
    ProductRepository productRepository;

    public EditBookViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    /**
     * @param id product id
     * @return product live data
     */
    public LiveData<Product> getProductById(String id) {
        return productRepository.getById(id);
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
     * @param product product object
     * @return task
     */
    public Task<Void> saveProduct(Product product) {
        return productRepository.save(product);
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
    public Task<Void> saveBookUri(String id, Uri uri) {
        return productRepository.saveBookUri(id, uri);
    }

    /**
     * initialize dagger injection
     *
     * @param application application to inject repository class
     */
    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
