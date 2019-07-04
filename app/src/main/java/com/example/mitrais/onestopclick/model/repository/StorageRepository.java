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
        return storageService.uploadProductThumbnail(uri, filename);
    }

    public Task<Uri> uploadBookFile(Uri uri, String filename) {
        return storageService.uploadBookFile(uri, filename);
    }

    public Task<Uri> uploadMusicFile(Uri uri, String filename) {
        return storageService.uploadMusicFile(uri, filename);
    }

    public Task<Uri> uploadTrailerFile(Uri uri, String filename) {
        return storageService.uploadTrailerFile(uri, filename);
    }

    public Task<Uri> uploadMovieFile(Uri uri, String filename) {
        return storageService.uploadMovieFile(uri, filename);
    }

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
