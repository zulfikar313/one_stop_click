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
    AuthRepository authRepo;

    @Inject
    ProfileRepository profileRepo;

    @Inject
    ProfileProductRepository profileProductRepo;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    Task<DocumentSnapshot> syncData(FirebaseUser user) {
        if (user != null)
            return profileRepo.sync(user.getEmail())
                    .addOnSuccessListener(documentSnapshot -> {
                        profileProductRepo.sync()
                                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
                    });
        else
            return null;
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
