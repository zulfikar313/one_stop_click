package com.example.mitrais.onestopclick.view.edit_music;

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

public class EditMusicViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    @Inject
    StorageRepository storageRepo;

    @Inject
    ProductRepository productRepo;

    public EditMusicViewModel(@NonNull Application application) {
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

    Task<Void> saveMusicUri(String id, Uri uri) {
        return productRepo.saveMusicUri(id, uri);
    }

    Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageRepo.uploadThumbnail(uri, filename);
    }

    Task<Uri> uploadMusic(Uri uri, String filename) {
        return storageRepo.uploadMusic(uri, filename);
    }

    Task<Void> saveRating(String id, HashMap<String, Float> rating) {
        return productRepo.saveRating(id, rating);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
