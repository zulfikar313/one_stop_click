package com.example.mitrais.onestopclick.view.search_product;

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

public class SearchProductViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    @Inject
    ProductRepository productRepository;

    @Inject
    ProfileProductRepository profileProductRepository;

    public SearchProductViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    LiveData<List<Product>> searchProduct(String search) {
        return productRepository.search(search);
    }

    LiveData<List<ProfileProduct>> getAllProfileProducts() {
        return profileProductRepository.getAll();
    }

    Task<Void> addLike(String productId) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepo.getUser().getEmail());
        profileProduct.setProductId(productId);
        profileProduct.setLiked(true);
        profileProduct.setDisliked(false);

        return profileProductRepository.save(profileProduct);
    }

    Task<Void> removeLike(String productId) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepo.getUser().getEmail());
        profileProduct.setProductId(productId);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(false);
        return profileProductRepository.save(profileProduct);
    }

    Task<Void> addDislike(String productId) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepo.getUser().getEmail());
        profileProduct.setProductId(productId);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(true);
        return profileProductRepository.save(profileProduct);
    }

    Task<Void> removeDislike(String productId) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepo.getUser().getEmail());
        profileProduct.setProductId(productId);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(false);
        return profileProductRepository.save(profileProduct);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
