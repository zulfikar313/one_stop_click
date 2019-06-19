package com.example.mitrais.onestopclick.view.edit_movie;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import javax.inject.Inject;

public class EditMovieViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    @Inject
    StorageRepository storageRepo;

    @Inject
    ProductRepository productRepo;

    public EditMovieViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    LiveData<Product> getProductById(String id) {
        return productRepo.getById(id);
    }

    Task<Void> saveProduct(Product product) {
        return productRepo.save(product);
    }

    Task<Void> saveThumbnailUri(String id, Uri uri) {
        return productRepo.saveThumbnailUri(id, uri);
    }

    Task<Void> saveTrailerUri(String id, Uri uri) {
        return productRepo.saveTrailerUri(id, uri);
    }

    Task<Void> saveMovieUri(String id, Uri uri) {
        return productRepo.saveMovieUri(id, uri);
    }

    Task<Void> saveRating(String id, HashMap<String, Integer> rating) {
        return productRepo.saveRating(id, rating);
    }

    Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageRepo.uploadThumbnail(uri, filename);
    }

    Task<Uri> uploadTrailer(Uri uri, String filename) {
        return storageRepo.uploadTrailer(uri, filename);
    }

    Task<Uri> uploadMovie(Uri uri, String filename) {
        return storageRepo.uploadMovie(uri, filename);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
