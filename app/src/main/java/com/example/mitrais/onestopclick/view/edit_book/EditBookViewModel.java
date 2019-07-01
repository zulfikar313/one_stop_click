package com.example.mitrais.onestopclick.view.edit_book;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.Comment;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class EditBookViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    @Inject
    ProfileRepository profileRepo;

    @Inject
    ProductRepository productRepo;

    @Inject
    StorageRepository storageRepo;

    public EditBookViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    LiveData<Profile> getProfile(String email) {
        return profileRepo.get(email);
    }

    Task<DocumentSnapshot> sync(String productId) {
        return productRepo.sync(productId);
    }

    Task<QuerySnapshot> syncComments(String productId) {
        return productRepo.syncComments(productId);
    }

    LiveData<Product> getProductById(String id) {
        return productRepo.getById(id);
    }

    LiveData<List<Comment>> getAllComments() {
        return productRepo.getAllComments();
    }

    LiveData<List<Comment>> getCommentsByProductId(String productId) {
        return productRepo.getCommentsByProductId(productId);
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

    Task<Void> saveRating(String id, HashMap<String, Float> rating) {
        return productRepo.saveRating(id, rating);
    }

    Task<DocumentReference> addComment(String productId, Comment comment) {
        return productRepo.addComment(productId, comment);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
