package com.example.mitrais.onestopclick.view.edit_music;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class EditMusicViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    @Inject
    ProfileRepository profileRepo;

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

    LiveData<Profile> getProfile(String email) {
        return profileRepo.getProfileByEmail(email);
    }

    Task<DocumentSnapshot> sync(String productId) {
        return productRepo.syncProductById(productId);
    }

    Task<QuerySnapshot> syncProfiles() {
        return profileRepo.syncAllProfiles();
    }

    LiveData<Product> getProductById(String id) {
        return productRepo.getProductById(id);
    }

    Task<Void> saveProduct(Product product) {
        return productRepo.saveProduct(product);
    }

    LiveData<List<Comment>> getAllComments() {
        return productRepo.getAllComments();
    }

    LiveData<List<Comment>> getCommentsByProductId(String productId) {
        return productRepo.getCommentsByProductId(productId);
    }

    CollectionReference getCommentReference(String productId) {
        return productRepo.getCommentReference(productId);
    }

    void insertComment(Comment comment) {
        productRepo.insertComment(comment);
    }

    void deleteComment(Comment comment) {
        productRepo.deleteComment(comment);
    }

    Task<Void> saveThumbnailUri(String id, Uri uri) {
        return productRepo.saveProductThumbnailUri(id, uri);
    }

    Task<Void> saveMusicUri(String id, Uri uri) {
        return productRepo.saveMusicFileUri(id, uri);
    }

    Task<Uri> uploadThumbnail(Uri uri, String filename) {
        return storageRepo.uploadThumbnail(uri, filename);
    }

    Task<Uri> uploadMusic(Uri uri, String filename) {
        return storageRepo.uploadMusicFile(uri, filename);
    }

    Task<Void> saveRating(String id, HashMap<String, Float> rating) {
        return productRepo.saveProductRating(id, rating);
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
