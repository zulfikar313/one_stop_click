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

/**
 * SplashViewModel handle data lifecycle for SplashActivity
 */
public class SplashViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    @Inject
    ProfileRepository profileRepository;

    /**
     * SplashViewModel constructor
     *
     * @param application application to inject repository class
     */
    public SplashViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    /**
     * get logged in user
     *
     * @return logged in user
     */
    public FirebaseUser getUser() {
        return authRepository.getUser();
    }

    /**
     * synchronized user data
     *
     * @param user logged in user
     * @return sync data task
     */
    public Task<DocumentSnapshot> syncData(FirebaseUser user) {
        if (user != null)
            return profileRepository.retrieveProfileByEmail(user.getEmail());
        else
            return null;
    }

    /**
     * initialize dagger injection
     *
     * @param application application to inject repository class
     */
    private void initDagger(Application application) {
        SplashViewModelComponent component = DaggerSplashViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
