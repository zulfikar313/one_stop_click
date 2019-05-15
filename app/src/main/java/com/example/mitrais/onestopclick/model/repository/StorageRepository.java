package com.example.mitrais.onestopclick.model.repository;

import android.net.Uri;

import com.example.mitrais.onestopclick.dagger.component.DaggerStorageRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.StorageRepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.StorageService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

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
     * @return task
     */
    public Task<Uri> uploadProfileImage(Uri uri, String filename) {
        return storageService.uploadProfileImage(uri, filename);
    }

    /**
     * @param uri      thumbnail uri
     * @param filename thumbnail filenanme
     * @return task
     */
    public Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageService.uploadThumbnail(uri, filename);
    }

    /**
     * @param uri      book uri
     * @param filename book filename
     * @return task
     */
    public Task<Uri> uploadBook(Uri uri, String filename) {
        return storageService.uploadBook(uri, filename);
    }

    /**
     * @param localUri book local uri
     * @param filename book filename
     * @return task
     */
    public FileDownloadTask downloadBook(Uri localUri, String filename) {
        return storageService.downloadBook(localUri, filename);
    }

    /**
     * @param uri      music uri
     * @param filename music filename
     * @return task
     */
    public Task<Uri> uploadMusic(Uri uri, String filename) {
        return storageService.uploadMusic(uri, filename);
    }

    /**
     * @param uri      trailer uri
     * @param filename trailer filename
     * @return task
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
