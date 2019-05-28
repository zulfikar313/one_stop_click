package com.example.mitrais.onestopclick.view.splash;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileProductRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

public class SplashViewModel extends AndroidViewModel {
    private static final String TAG = "SplashViewModel";

    @Inject
    AuthRepository authRepository;

    @Inject
    ProfileRepository profileRepository;

    @Inject
    ProfileProductRepository profileProductRepository;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    public FirebaseUser getUser() {
        return authRepository.getUser();
    }

    /**
     * @param user logged in user
     * @return sync data task
     */
    public Task<DocumentSnapshot> syncData(FirebaseUser user) {
        if (user != null)
            return profileRepository.sync(user.getEmail())
                    .addOnSuccessListener(documentSnapshot -> {
                        profileProductRepository.sync()
                                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
                    });
        else
            return null;
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
