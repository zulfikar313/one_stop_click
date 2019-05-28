package com.example.mitrais.onestopclick.view.main.product_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.ProfileProduct;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileProductRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import javax.inject.Inject;

public class ProductListViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    @Inject
    ProductRepository productRepo;

    @Inject
    ProfileProductRepository profileProductRepo;

    public ProductListViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    LiveData<List<Product>> getAllProducts() {
        return productRepo.getAll();
    }

    LiveData<List<Product>> getProductsByType(String type) {
        return productRepo.getByType(type);
    }

    LiveData<List<Product>> getProductsByGenre(String genre) {
        return productRepo.getByGenre(genre);
    }

    LiveData<List<Product>> getProductsByTypeAndGenre(String type, String genre) {
        return productRepo.getByTypeAndGenre(type, genre);
    }

    LiveData<List<ProfileProduct>> getAllProfileProducts() {
        return profileProductRepo.getAll();
    }

    Task<Void> addLike(String id) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepo.getUser().getEmail());
        profileProduct.setProductId(id);
        profileProduct.setLiked(true);
        profileProduct.setDisliked(false);
        return profileProductRepo.save(profileProduct);
    }

    Task<Void> removeLike(String productId) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepo.getUser().getEmail());
        profileProduct.setProductId(productId);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(false);
        return profileProductRepo.save(profileProduct);
    }

    Task<Void> addDislike(String productId) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepo.getUser().getEmail());
        profileProduct.setProductId(productId);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(true);
        return profileProductRepo.save(profileProduct);
    }

    Task<Void> removeDislike(String productId) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepo.getUser().getEmail());
        profileProduct.setProductId(productId);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(false);
        return profileProductRepo.save(profileProduct);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
