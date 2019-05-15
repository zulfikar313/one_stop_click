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
    AuthRepository authRepository;

    @Inject
    ProfileRepository profileRepository;

    @Inject
    ProductRepository productRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    public void logout() {
        authRepository.logout();
    }

    public FirebaseUser getUser() {
        return authRepository.getUser();
    }

    /**
     * @return sync user data task
     */
    public Task<QuerySnapshot> syncUserData() {
        return productRepository.syncProducts();
    }

    /**
     * @param email user email address
     * @return profile live data
     */
    public LiveData<Profile> getProfile(String email) {
        return profileRepository.getProfile(email);
    }

    public void deleteUserData(){
        profileRepository.deleteProfile();
    }

    /**
     * initialize dagger injection
     *
     * @param application application to inject repository class
     */
    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
