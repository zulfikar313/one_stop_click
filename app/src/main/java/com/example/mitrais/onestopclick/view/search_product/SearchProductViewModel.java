package com.example.mitrais.onestopclick.view.search_product;

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

public class SearchProductViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    @Inject
    ProfileRepository profileRepo;

    @Inject
    ProductRepository productRepo;

    public SearchProductViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    LiveData<Profile> getProfileByEmail(String email) {
        return profileRepo.get(email);
    }

    LiveData<List<Product>> searchProduct(String search) {
        return productRepo.search(search);
    }

    void addView(Product product) {
        String email = authRepo.getUser().getEmail();
        ArrayList<String> viewedBy = product.getViewedBy();

        if (!viewedBy.contains(email))
            viewedBy.add(email);

        product.setViewedBy(viewedBy);

        productRepo.save(product);
    }

    Task<Void> addPutInCartBy(Product product) {
        String email = authRepo.getUser().getEmail();
        ArrayList<String> putInCartBy = product.getPutInCartBy();

        if (!putInCartBy.contains(email))
            putInCartBy.add(email);

        product.setPutInCartBy(putInCartBy);
        return productRepo.save(product);
    }

    Task<Void> removePutInCartBy(Product product) {
        String email = authRepo.getUser().getEmail();
        ArrayList<String> putInCartBy = product.getPutInCartBy();

        if (putInCartBy.contains(email))
            putInCartBy.remove(email);

        product.setPutInCartBy(putInCartBy);
        return productRepo.save(product);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
