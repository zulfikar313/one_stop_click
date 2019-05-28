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
    AuthRepository authRepository;

    @Inject
    ProductRepository productRepository;

    @Inject
    ProfileProductRepository profileProductRepository;

    public ProductListViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    public FirebaseUser getUser() {
        return authRepository.getUser();
    }

    public LiveData<List<Product>> getAllProducts() {
        return productRepository.getAll();
    }

    public LiveData<List<Product>> getProductsByType(String type) {
        return productRepository.getByType(type);
    }

    public LiveData<List<Product>> getProductsByGenre(String genre) {
        return productRepository.getByGenre(genre);
    }

    public LiveData<List<Product>> getProductsByTypeAndGenre(String type, String genre) {
        return productRepository.getByTypeAndGenre(type, genre);
    }

    public LiveData<List<ProfileProduct>> getAllProfileProducts() {
        return profileProductRepository.getAll();
    }

    public Task<Void> addLike(String id) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepository.getUser().getEmail());
        profileProduct.setProductId(id);
        profileProduct.setLiked(true);
        profileProduct.setDisliked(false);

        return profileProductRepository.save(profileProduct);
    }

    public Task<Void> removeLike(String id) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepository.getUser().getEmail());
        profileProduct.setProductId(id);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(false);

        return profileProductRepository.save(profileProduct);
    }

    public Task<Void> addDislike(String id) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepository.getUser().getEmail());
        profileProduct.setProductId(id);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(true);

        return profileProductRepository.save(profileProduct);
    }

    public Task<Void> removeDislike(String id) {
        ProfileProduct profileProduct = new ProfileProduct();
        profileProduct.setEmail(authRepository.getUser().getEmail());
        profileProduct.setProductId(id);
        profileProduct.setLiked(false);
        profileProduct.setDisliked(false);

        return profileProductRepository.save(profileProduct);
    }

    /**
     * initialize dagger injection
     *
     * @param application for repository injection
     */
    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
