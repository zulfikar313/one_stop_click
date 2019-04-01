package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerRegistrationViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.RegistrationViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class RegistrationViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        initDagger();
    }

    // initialize dagger injection
    private void initDagger() {
        RegistrationViewModelComponent component = DaggerRegistrationViewModelComponent.builder()
                .build();
        component.inject(this);
    }

    // region public methods
    // get logged in user
    public FirebaseUser getCurrentUser() {
        return authRepository.getUser();
    }

    // register new user with email and password
    public Task<AuthResult> register(String email, String password) {
        return authRepository.register(email, password);
    }

    // send email verification to user email address
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authRepository.sendVerificationEmail(user);
    }
    //endregion
}
