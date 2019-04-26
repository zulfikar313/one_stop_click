package com.example.mitrais.onestopclick.model.repository;

import android.net.Uri;

import com.example.mitrais.onestopclick.dagger.component.DaggerStorageRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.StorageRepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.StorageService;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

/**
 * StorageRepository class provide access to StorageService
 */
public class StorageRepository {
    @Inject
    StorageService storageService;

    /**
     * StorageRepository constructor
     */
    public StorageRepository() {
        initDagger();
    }

    /**
     * upload profile image and returns save profile image task
     * with image uri within
     *
     * @param uri      profile image uri
     * @param filename profile image filename
     * @return save profile image task and image uri
     */
    public Task<Uri> uploadProfileImage(Uri uri, String filename) {
        return storageService.setProfileImage(uri, filename);
    }

    /**
     * upload product image and returns save product image task
     * with image uri within
     *
     * @param uri      product image uri
     * @param filename product image filename
     * @return save product image task and image uri
     */
    public Task<Uri> uploadProductImage(Uri uri, String filename) {
        return storageService.setProductImage(uri, filename);
    }


    /**
     * @param uri      trailer uri
     * @param filename trailer filename
     * @return
     */
    public Task<Uri> uploadTrailer(Uri uri, String filename) {
        return storageService.uploadTrailer(uri, filename);
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        StorageRepositoryComponent component = DaggerStorageRepositoryComponent.builder()
                .build();
        component.inject(this);
    }
}
