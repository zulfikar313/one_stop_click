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
    StorageRepository storageRepo;

    @Inject
    ProductRepository productRepo;

    public EditBookViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    LiveData<Product> getProductById(String id) {
        return productRepo.getById(id);
    }

    FileDownloadTask downloadBook(Uri localUri, String filename) {
        return storageRepo.downloadBook(localUri, filename);
    }

    Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageRepo.uploadThumbnail(uri, filename);
    }

    Task<Uri> uploadBook(Uri uri, String filename) {
        return storageRepo.uploadBook(uri, filename);
    }

    Task<Void> saveProduct(Product product) {
        return productRepo.save(product);
    }

    Task<Void> saveThumbnailUri(String id, Uri uri) {
        return productRepo.saveThumbnailUri(id, uri);
    }

    Task<Void> saveBookUri(String id, Uri uri) {
        return productRepo.saveBookUri(id, uri);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
