package com.example.mitrais.onestopclick.view.registration;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class RegistrationViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    public FirebaseUser getUser() {
        return authRepo.getUser();
    }

    public Task<AuthResult> register(String email, String password) {
        return authRepo.register(email, password);
    }

    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authRepo.sendVerificationEmail(user);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
