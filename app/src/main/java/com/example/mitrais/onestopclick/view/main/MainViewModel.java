package com.example.mitrais.onestopclick.view.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
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
    AuthRepository authRepo;

    @Inject
    ProfileRepository profileRepo;

    @Inject
    ProductRepository productRepo;

    public MainViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    void logout() {
        authRepo.logout();
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    Task<QuerySnapshot> syncProducts() {
        return productRepo.syncAllProducts();
    }

    LiveData<Profile> getProfile(String email) {
        return profileRepo.getProfileByEmail(email);
    }

    void deleteUserData() {
        profileRepo.deleteProfile();
        // TODO: delete bound data tied to logged in user
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
