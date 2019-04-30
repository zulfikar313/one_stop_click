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

    /**
     * @param uri      profile image uri
     * @param filename profile image filename
     * @return upload profile image task and image uri
     */
    public Task<Uri> uploadProfileImage(Uri uri, String filename) {
        return storageService.uploadProfileImage(uri, filename);
    }

    /**
     * @param uri      thumbnail uri
     * @param filename thumbnail filename
     * @return upload thumnail task
     */
    public Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageService.uploadThumbnail(uri, filename);
    }


    /**
     * @param uri      trailer uri
     * @param filename trailer filename
     * @return upload tralier task
     */
    public Task<Uri> uploadTrailer(Uri uri, String filename) {
        return storageService.uploadTrailer(uri, filename);
    }

    /**
     * @param uri      music uri
     * @param filename music filename
     * @return upload music task
     */
    public Task<Uri> uploadMusic(Uri uri, String filename) {
        return storageService.uploadMusic(uri, filename);
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
