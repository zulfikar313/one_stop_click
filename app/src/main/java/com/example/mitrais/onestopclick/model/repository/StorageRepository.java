package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.net.Uri;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.StorageService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import javax.inject.Inject;

public class StorageRepository {
    @Inject
    StorageService storageService;

    public StorageRepository(Application application) {
        initDagger(application);
    }

    public FileDownloadTask downloadBook(Uri localUri, String filename) {
        return storageService.downloadBook(localUri, filename);
    }

    public Task<Uri> uploadProfileImage(Uri uri, String filename) {
        return storageService.uploadProfileImage(uri, filename);
    }

    public Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageService.uploadThumbnail(uri, filename);
    }

    public Task<Uri> uploadBook(Uri uri, String filename) {
        return storageService.uploadBook(uri, filename);
    }

    public Task<Uri> uploadMusic(Uri uri, String filename) {
        return storageService.uploadMusic(uri, filename);
    }

    public Task<Uri> uploadTrailer(Uri uri, String filename) {
        return storageService.uploadTrailer(uri, filename);
    }

    public Task<Uri> uploadMovie(Uri uri, String filename) {
        return storageService.uploadMovie(uri, filename);
    }

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
