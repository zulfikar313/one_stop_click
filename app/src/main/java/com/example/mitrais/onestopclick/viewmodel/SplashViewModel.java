package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerSplashViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.SplashViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

public class SplashViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    @Inject
    ProfileRepository profileRepository;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    // gets logged in user
    public FirebaseUser getUser() {
        return authRepository.getUser();
    }

    // sync data with firebase
    public Task<DocumentSnapshot> syncData(FirebaseUser user) {
        if (user != null)
            return profileRepository.getProfileByEmail(user.getEmail());
        else
            return null;
    }

    // initialize dagger injection
    private void initDagger(Application application) {
        SplashViewModelComponent component = DaggerSplashViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
