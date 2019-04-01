package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerMainViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.MainViewModelComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

public class MainViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    @Inject
    ProfileRepository profileRepository;

    @Inject
    ProductRepository productRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);

        // Initialize dagger injection
        MainViewModelComponent component = DaggerMainViewModelComponent.builder()
                .application(application)
                .build();

        component.inject(this);
    }

    // logout from current user
    public void logout() {
        authRepository.logout();
    }

    // get currently logged in user
    public FirebaseUser getCurrentUser() {
        return authRepository.getUser();
    }

    // synchronize product data
    public Task<QuerySnapshot> syncProductData() {
        return productRepository.getAllProducts();
    }

    // get profile live data
    public LiveData<Profile> getProfileByEmail(String email) {
        return profileRepository.retrieveProfileByEmail(email);
    }
}
