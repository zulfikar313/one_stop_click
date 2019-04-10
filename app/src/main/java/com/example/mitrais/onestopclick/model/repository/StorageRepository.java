package com.example.mitrais.onestopclick.model.repository;

import android.net.Uri;

import com.example.mitrais.onestopclick.dagger.component.DaggerStorageRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.StorageRepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.StorageService;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class StorageRepository {
    @Inject
    StorageService storageService;

    public StorageRepository() {
        initDagger();
    }

    // save profile image
    public Task<Uri> saveProfileImage(Uri uri, String fileName) {
        return storageService.saveProfileImage(uri, fileName);
    }

    // save product image
    public Task<Uri> saveProductImage(Uri uri, String fileName) {
        return storageService.saveProductImage(uri, fileName);
    }

    // initialize dagger injection
    private void initDagger() {
        StorageRepositoryComponent component = DaggerStorageRepositoryComponent.builder()
                .build();
        component.inject(this);
    }
}
