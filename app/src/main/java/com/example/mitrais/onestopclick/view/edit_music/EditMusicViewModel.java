package com.example.mitrais.onestopclick.view.edit_music;

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

import javax.inject.Inject;

public class EditMusicViewModel extends AndroidViewModel {

    @Inject
    StorageRepository storageRepository;

    @Inject
    ProductRepository productRepository;

    public EditMusicViewModel(@NonNull Application application) {
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
     * @param product product object
     * @return task
     */
    public Task<Void> saveProduct(Product product) {
        return productRepository.saveProduct(product);
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
     * @param id  product id
     * @param uri thumbnail uri
     * @return save product task
     */
    public Task<Void> saveThumbnailUri(String id, Uri uri) {
        return productRepository.saveThumbnailUri(id, uri);
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
     * @param id  product id
     * @param uri music uri
     * @return task
     */
    public Task<Void> saveMusicUri(String id, Uri uri) {
        return productRepository.saveMusicUri(id, uri);
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
