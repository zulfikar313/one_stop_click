package com.example.mitrais.onestopclick.view.cart;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CartViewModel extends AndroidViewModel {
    private LiveData<List<Product>> products;

    @Inject
    AuthRepository authRepo;

    @Inject
    ProfileRepository profileRepo;

    @Inject
    ProductRepository productRepo;

    public CartViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
        products = productRepo.getProductInCart();
    }

    public FirebaseUser getUser() {
        return authRepo.getUser();
    }

    public LiveData<Profile> getProfile(String email) {
        return profileRepo.getProfileByEmail(email);
    }

    LiveData<List<Product>> getProductsInCart() {
        return products;
    }

    Task<Void> saveProduct(Product product) {
        return productRepo.saveProduct(product);
    }

    void removePutInCartBy(Product product) {
        String email = authRepo.getUser().getEmail();
        ArrayList<String> putInCartBy = product.getPutInCartBy();
        putInCartBy.remove(email);
        product.setPutInCartBy(putInCartBy);
        productRepo.saveProduct(product);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
